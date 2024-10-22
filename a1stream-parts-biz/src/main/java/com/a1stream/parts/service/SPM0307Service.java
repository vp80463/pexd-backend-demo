/**
 *
 */
package com.a1stream.parts.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.PartsSalesStockAllocationManager;
import com.a1stream.domain.bo.parts.TargetSalesOrderItemBO;
import com.a1stream.domain.form.parts.SPM030701Form;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.ProductCostRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Service
public class SPM0307Service {

    @Resource
    ProductInventoryRepository productInventoryRepository;

    @Resource
    LocationRepository locationRepository;

    @Resource
    ProductStockStatusRepository productStockStatusRepository;

    @Resource
    InventoryManager inventoryManager;

    @Resource
    ProductCostRepository productCostRepository;

    @Resource
    PartsSalesStockAllocationManager partsSalesStockAllocationManager;

    @Resource
    SalesOrderItemRepository salesOrderItemRepository;

    public ProductCostVO findByProductIdAndCostTypeAndSiteId(SPM030701Form model) {
        return BeanMapUtils.mapTo(productCostRepository.findByProductIdAndCostTypeAndSiteId(model.getPartsId(), CostType.AVERAGE_COST, model.getSiteId()),ProductCostVO.class);

    }

    public void doPartsStockAdjustment(SPM030701Form model) {


        BigDecimal onHandQuantity = BigDecimal.ZERO;

        ProductStockStatusVO onHandVo =BeanMapUtils.mapTo(productStockStatusRepository.findProductStockStatus(model.getSiteId(),model.getPointId(),model.getPartsId(),PJConstants.SpStockStatus.ONHAND_QTY.getCodeDbid()), ProductStockStatusVO.class);

        if(onHandVo!=null){
            onHandQuantity = onHandVo.getQuantity() ;
        }

        if(StringUtils.equals(model.getAdjustmentType(),PJConstants.StockAdjustmentType.ADJUSTMENTTOFROZEN.getCodeDbid())) {

            ProductInventoryVO fromInventoryVO = this.findProductInventoryByLocationId(model.getPointId(), model.getPartsId(), model.getSiteId(), model.getFromLocationId());
            ProductInventoryVO toInventoryVO = this.findProductInventoryByLocationId(model.getPointId(), model.getPartsId(), model.getSiteId(), model.getToLocationId());

            //调用共通方法保存productstockstatus
            Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();
            inventoryManager.generateStockStatusVOMap(model.getSiteId(), model.getPointId(), model.getPartsId(), model.getAdjustmentQty().negate(), PJConstants.SpStockStatus.ONHAND_QTY.getCodeDbid(), stockStatusVOChangeMap);
            inventoryManager.generateStockStatusVOMap(model.getSiteId(), model.getPointId(), model.getPartsId(), model.getAdjustmentQty(), PJConstants.SpStockStatus.ONFROZEN_QTY.getCodeDbid(), stockStatusVOChangeMap);
            inventoryManager.updateProductStockStatusByMap(stockStatusVOChangeMap);

            //减库存
            inventoryManager.doUpdateProductInventoryQtyByCondition(model.getSiteId()
                    , model.getPointId()
                    , model.getPartsId()
                    , model.getFromLocationId()
                    , model.getAdjustmentQty()
                    , fromInventoryVO
                    , CommonConstants.CHAR_MINUS);
             //加库存
            inventoryManager.doUpdateProductInventoryQtyByCondition(model.getSiteId()
                    , model.getPointId()
                    , model.getPartsId()
                    , model.getToLocationId()
                    , model.getAdjustmentQty()
                    , toInventoryVO
                    , CommonConstants.CHAR_PLUS);

            ProductInventoryVO inventoryVO = BeanMapUtils.mapTo(productInventoryRepository.findFirstByFacilityIdAndProductIdAndSiteIdAndLocationId(model.getPointId(),model.getPartsId(),model.getSiteId(),model.getFromLocationId()), ProductInventoryVO.class);
            if (inventoryVO.getQuantity().compareTo(BigDecimal.ZERO) == 0 && StringUtils.equals(inventoryVO.getPrimaryFlag(), CommonConstants.CHAR_N)) {
                productInventoryRepository.deleteById(inventoryVO.getProductInventoryId());
            }
            inventoryManager.doLocationStockMovement(model.getSiteId(),model.getPartsId(), model.getPointId(), model.getFromLocationId(), model.getToLocationId(), model.getAdjustmentQty(), model.getPersonId());

        }else{
            if(StringUtils.equals(model.getCheckForMinus(), CommonConstants.CHAR_N)) {
                //库存调整加
                inventoryManager.doStockAdjustPlus(model.getSiteId(), model.getPointId(), model.getAdjustmentType(), model.getFromLocationId(), model.getToLocationId(), model.getPartsId(), model.getReason(), model.getAdjustmentQty(), model.getPartsCost(),model.getPersonId(),model.getPersonNm());
                List<TargetSalesOrderItemBO> targetSalesOrderItemBOList = salesOrderItemRepository.getWaitingAllocateSoItemList(model.getSiteId(), model.getPointId(), model.getPartsId());

                if(!ObjectUtils.isEmpty(targetSalesOrderItemBOList)) {

                    partsSalesStockAllocationManager.doBoRelease(model.getSiteId(), model.getPointId(), targetSalesOrderItemBOList);
                }
            }else {
                //库存调整减
                inventoryManager.doStockAdjustMinus(model.getSiteId(), model.getPointId(), model.getAdjustmentType(), model.getFromLocationId(), model.getToLocationId(), model.getPartsId(), model.getReason(), model.getAdjustmentQty(),model.getPersonId(),model.getPersonNm());
                ProductInventoryVO inventoryVO = BeanMapUtils.mapTo(productInventoryRepository.findFirstByFacilityIdAndProductIdAndSiteIdAndLocationId(model.getPointId(),model.getPartsId(),model.getSiteId(),model.getFromLocationId()), ProductInventoryVO.class);
                if (inventoryVO.getQuantity().compareTo(BigDecimal.ZERO) == 0 && StringUtils.equals(inventoryVO.getPrimaryFlag(), CommonConstants.CHAR_N)) {
                    productInventoryRepository.deleteById(inventoryVO.getProductInventoryId());
                }

            }
        }

        //当在库数不够时，需要将已预定的数量去掉来进行补足
        if(StringUtils.equals(model.getCheckForMinus(), CommonConstants.CHAR_Y) && model.getAdjustmentQty().compareTo(onHandQuantity) == CommonConstants.INTEGER_ONE){
            BigDecimal qty = model.getAdjustmentQty().subtract(onHandQuantity);
            partsSalesStockAllocationManager.executeCancelAllocated(model.getSiteId(),model.getPointId(),model.getPartsId(),qty);
        }
    }




    public ProductInventoryVO findProductInventory(Long facilityId, Long productId, String siteId, Long locationId) {

        return BeanMapUtils.mapTo(productInventoryRepository.findFirstByFacilityIdAndProductIdAndSiteIdAndLocationId(facilityId,productId,siteId,locationId), ProductInventoryVO.class);
    }

    public List<ProductInventoryVO> findProductInventoryByProductId(Long facilityId, Long productId, String siteId) {

        return BeanMapUtils.mapListTo(productInventoryRepository.findByFacilityIdAndProductIdAndSiteId(facilityId,productId,siteId), ProductInventoryVO.class);
    }

    public LocationVO findLocation(Long locationId) {

        return BeanMapUtils.mapTo(locationRepository.findById(locationId), LocationVO.class);
    }

    public ProductInventoryVO findProductInventoryByLocationId(Long facilityId, Long productId, String siteId, Long locationId) {

        return BeanMapUtils.mapTo(productInventoryRepository.findByFacilityIdAndProductIdAndSiteIdAndLocationId(facilityId,productId,siteId,locationId), ProductInventoryVO.class);
    }

    public List<ProductStockStatusVO>  findProductStockStatusIn(String siteId , Long facilityId, Long productId, List<String> productStockStatusType){

        return BeanMapUtils.mapListTo(productStockStatusRepository.findProductStockStatusIn(siteId,facilityId,productId,productStockStatusType), ProductStockStatusVO.class);
    }



}
