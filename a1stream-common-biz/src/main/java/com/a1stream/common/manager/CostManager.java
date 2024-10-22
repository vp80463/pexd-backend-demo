package com.a1stream.common.manager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.model.ConstantsBO;
import com.a1stream.domain.entity.InventoryTransaction;
import com.a1stream.domain.entity.ProductCost;
import com.a1stream.domain.repository.InventoryTransactionRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductCostRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.vo.InventoryTransactionVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;

@Component
public class CostManager {

    @Resource
    private ProductCostRepository productCostRepo;

    @Resource
    private MstProductRepository productRepo;

    @Resource
    private ProductStockStatusRepository productStockStsRepo;

    @Resource
    private InventoryTransactionRepository inventoryTransRepo;

    @Resource
    private ConstantsLogic constantsLogic;

    public ProductCostVO doCostCalculation(String siteId
                                    , Long receiptProductId
                                    , BigDecimal receiptPrice
                                    , BigDecimal receiptQty
                                    , BigDecimal receivePercent
                                    , Long receiveFacilityId) {

        if (receiptProductId == null) {
            return null;
        }

        MstProductVO productVO = BeanMapUtils.mapTo(productRepo.findByProductId(receiptProductId), MstProductVO.class);
        String productCd = productVO != null? productVO.getProductCd() : StringUtils.EMPTY;
        String productNm = productVO != null? productVO.getSalesDescription() : StringUtils.EMPTY;

        // 更新原有的receiveProductCostVO（product_cost: cost_type = S011RECEIVECOST），若不存在记录则新建
        ProductCostVO receiveProductCostVO = BeanMapUtils.mapTo(productCostRepo.findByProductIdAndCostTypeAndSiteId(receiptProductId, CostType.RECEIVE_COST, siteId), ProductCostVO.class);
        if (receiveProductCostVO == null) {
            receiveProductCostVO = buildProductCostVO(siteId, receiptProductId, receiptPrice, productCd, productNm, CostType.RECEIVE_COST);
        } else {
            receiveProductCostVO.setCost(receiptPrice);
        }

        // find avgProductCostVO（product_cost: cost_type = S011AVERAGECOST），不存在则新建
        ProductCostVO avgProductCostVO = BeanMapUtils.mapTo(productCostRepo.findByProductIdAndCostTypeAndSiteId(receiptProductId, CostType.AVERAGE_COST, siteId), ProductCostVO.class);
        if (avgProductCostVO == null) {
            avgProductCostVO = buildProductCostVO(siteId, receiptProductId, receiptPrice, productCd, productNm, CostType.AVERAGE_COST);
        } else {
            List<ProductStockStatusVO> stockStatusVOList = BeanMapUtils.mapListTo(productStockStsRepo.findStockStatusListOneProduct(siteId
                                                                                                                                    , receiptProductId
                                                                                                                                    , ProductClsType.PART.getCodeDbid()
                                                                                                                                    , paramStockStatusTypeIn()), ProductStockStatusVO.class);

            BigDecimal stockQty = stockStatusVOList.stream().map(item -> item.getQuantity() != null ? item.getQuantity() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // stockAmount=avgProductCostVO.cost*stockQty;
            // receiptAmount=receiptPrice*receiptQty*(1+receivePercent);
            // 计算cost=(stockAmount+receiptAmount).divide(stockQty+receiptQty),4,4)
            BigDecimal stockAmount = avgProductCostVO.getCost().multiply(stockQty);
            BigDecimal receiptAmount = receiptPrice.multiply(receiptQty).multiply(BigDecimal.ONE.add(receivePercent));
            BigDecimal sumQty = stockQty.add(receiptQty);
            if (sumQty.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal cost = (stockAmount.add(receiptAmount)).divide(sumQty, 4, RoundingMode.HALF_UP);
                avgProductCostVO.setCost(cost);
            }

           // 根据Step2计算平均成本，当Step2查找到的productCosTVO不为空时，需要计算平衡成本，如果平衡成本<>0,则生成inventoryTransactionVO（S027COSTOFBALANCE）
           // avgProductCostVO.cost*(stockQty+receiptQty)-(stockAmount+receiptAmount)
            BigDecimal balanceCost = avgProductCostVO.getCost().multiply(stockQty.add(receiptQty)).subtract(stockAmount.add(receiptAmount));
            if (balanceCost.compareTo(BigDecimal.ZERO) != 0) {
                insertInventoryTransaction(siteId, receiptProductId, receiveFacilityId, productCd, productNm, balanceCost);
            }
        }

        productCostRepo.save(BeanMapUtils.mapTo(receiveProductCostVO, ProductCost.class));
        productCostRepo.save(BeanMapUtils.mapTo(avgProductCostVO, ProductCost.class));

        return avgProductCostVO;
    }

    private void insertInventoryTransaction(String siteId
                                            , Long receiptProductId
                                            , Long receiveFacilityId
                                            , String productCd
                                            , String productNm
                                            , BigDecimal balanceCost) {

        InventoryTransactionVO invTranVO = new InventoryTransactionVO();

        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);
        String sysTime = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M);
        Map<String, ConstantsBO> inventoryTransMap = constantsLogic.getConstantsMap(InventoryTransactionType.class.getDeclaredFields());
        String transactionType = InventoryTransactionType.COSTOFBALANCE.getCodeDbid();
        String invTransNm = inventoryTransMap.containsKey(transactionType)? inventoryTransMap.get(transactionType).getCodeData1() : "";

        invTranVO.setSiteId(siteId);
        invTranVO.setPhysicalTransactionDate(sysDate);
        invTranVO.setPhysicalTransactionTime(sysTime);
        invTranVO.setToFacilityId(receiveFacilityId);
        invTranVO.setProductId(receiptProductId);
        invTranVO.setProductCd(productCd);
        invTranVO.setProductNm(productNm);
        invTranVO.setTargetFacilityId(receiveFacilityId);

        invTranVO.setInventoryTransactionType(transactionType);
        invTranVO.setInventoryTransactionNm(invTransNm);
        invTranVO.setInCost(balanceCost);
        invTranVO.setProductClassification(ProductClsType.PART.getCodeDbid());

        inventoryTransRepo.save(BeanMapUtils.mapTo(invTranVO, InventoryTransaction.class));
    }

    public ProductCostVO buildProductCostVO(String siteId, Long receiptProductId, BigDecimal receiptPrice, String productCd, String productNm, String costType) {

        ProductCostVO productCostVO = new ProductCostVO();

        productCostVO.setSiteId(siteId);
        productCostVO.setProductId(receiptProductId);

        productCostVO.setProductCd(productCd);
        productCostVO.setProductNm(productNm);
        productCostVO.setCostType(costType);
        productCostVO.setCost(receiptPrice);
        productCostVO.setProductClassification(ProductClsType.PART.getCodeDbid());

        return productCostVO;
    }

    private Set<String> paramStockStatusTypeIn() {

        return new HashSet<>(Arrays.asList(SpStockStatus.ONHAND_QTY.getCodeDbid()        // S018ONHANDQTY
                                            ,SpStockStatus.ONCANVASSING_QTY.getCodeDbid()  // S018ONCANVASSINGQTY
                                            ,SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid()  // S018ONTRANSFERINQTY
                                            ,SpStockStatus.ONFROZEN_QTY.getCodeDbid()       // S018ONFROZENQTY
                                            ,SpStockStatus.ALLOCATED_QTY.getCodeDbid()      // S018ALLOCATEDQTY
                                            ,SpStockStatus.SHIPPING_REQUEST.getCodeDbid()   // S018SHIPPINGREQUEST
                                            ,SpStockStatus.ONSHIPPING.getCodeDbid()         // S018ONSHIPPING
                                            ,SpStockStatus.ONBORROWING_QTY.getCodeDbid()    // S018ONBORROWINGQTY
                                            ,SpStockStatus.ONSERVICE_QTY.getCodeDbid()      // S018ONSERVICEQTY
                                            ,SpStockStatus.ONPICKING_QTY.getCodeDbid()      // S018ONPICKINGQTY
                                            ,SpStockStatus.ONRECEIVING_QTY.getCodeDbid()    // S018ONRECEIVINGQTY
                                ));
    }

	public Map<Long, BigDecimal> getProductCostInBulk(String siteId, String costTypeId, Set<Long> targetProductIdList){

	    Map<Long, BigDecimal> result = new HashMap<>();
        List<ProductCost> productCostInfos = this.productCostRepo.findByProductIdInAndCostTypeAndSiteId(targetProductIdList, costTypeId, siteId);

        for (ProductCost member : productCostInfos){
            result.put(member.getProductId(), member.getCost());
        }

        return result;

	}
}