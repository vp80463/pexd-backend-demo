package com.a1stream.common.manager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.bo.PickingResultItemBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.InOutType;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.LocationMoveType;
import com.a1stream.common.constants.PJConstants.LocationType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.PurchaseOrderPriorityType;
import com.a1stream.common.constants.PJConstants.SdStockStatus;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.constants.PJConstants.StockAdjustReasonType;
import com.a1stream.common.constants.PJConstants.StockAdjustmentType;
import com.a1stream.common.exception.PJCustomException;
import com.a1stream.common.listener.model.ThreadLocalPJAuditableDetailAccessor;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.model.BaseResult;
import com.a1stream.common.model.ConstantsBO;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.entity.DeliveryOrder;
import com.a1stream.domain.entity.InventoryTransaction;
import com.a1stream.domain.entity.Location;
import com.a1stream.domain.entity.MstFacility;
import com.a1stream.domain.entity.MstProduct;
import com.a1stream.domain.entity.ProductCost;
import com.a1stream.domain.entity.ProductInventory;
import com.a1stream.domain.entity.ProductStockStatus;
import com.a1stream.domain.entity.StockLocationTransaction;
import com.a1stream.domain.repository.DeliveryOrderRepository;
import com.a1stream.domain.repository.InventoryTransactionRepository;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductCostRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.ReceiptSlipItemRepository;
import com.a1stream.domain.repository.ReceiptSlipRepository;
import com.a1stream.domain.repository.StockLocationTransactionRepository;
import com.a1stream.domain.repository.StoringLineItemRepository;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.InventoryTransactionVO;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.ReceiptSlipItemVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.StockLocationTransactionVO;
import com.a1stream.domain.vo.StoringLineItemVO;
import com.a1stream.domain.vo.StoringLineVO;
import com.a1stream.ifs.bo.SpPurchaseCancelModelBO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.IdUtils;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InventoryManager {

    @Resource
    private ProductStockStatusRepository productStockStsRepo;

    @Resource
    private ProductInventoryRepository productInventoryRepo;

    @Resource
    private MstFacilityRepository facilityRepo;

    @Resource
    private InventoryTransactionRepository inventoryTransRepo;

    @Resource
    private MstProductRepository productRepo;

    @Resource
    private ReceiptSlipRepository receiptSlipRepo;

    @Resource
    private ReceiptSlipItemRepository receiptSlipItemRepo;

    @Resource
    private LocationRepository locationRepo;

    @Resource
    private DeliveryOrderRepository deliveryOrderRepo;

    @Resource
    private ProductCostRepository productCostRepo;

    @Resource
    private StoringLineItemRepository storingLineItemRepo;

    @Resource
    private StockLocationTransactionRepository stockLocTransRepo;

    @Resource
    private CostManager costMgr;

    @Resource
    private ConstantsLogic constantsLogic;

    @Resource
    private GenerateNoManager generateNoMgr;

    public static final String MINUS = "MINUS";
    public static final String PLUS  = "PLUS";
    public static final String OUT = "OUT";

    /**
     *
     * @param siteId
     * @param facilityId
     * @param productId
     * @param productStockStatusType
     * @param minusQty
     */
    public void doUpdateProductStockStatusMinusQty(String siteId
                                                , Long facilityId
                                                , Long productId
                                                , String productStockStatusType
                                                , BigDecimal minusQty) {

        minusQty = minusQty != null? minusQty : BigDecimal.ZERO;
        ProductStockStatusVO productStockStsVO = BeanMapUtils.mapTo(productStockStsRepo.findProductStockStatus(siteId, facilityId, productId, productStockStatusType), ProductStockStatusVO.class);

        if (productStockStsVO != null) {
            if (productStockStsVO.getQuantity().compareTo(minusQty) < 0) {
                MstFacility facility = facilityRepo.findByFacilityId(productStockStsVO.getFacilityId());
                MstProduct product = productRepo.findByProductId(productStockStsVO.getProductId());
                Map<String, ConstantsBO> spStockStatusMap = constantsLogic.getConstantsMap(SpStockStatus.class.getDeclaredFields());
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00316", new String[]{
                        CodedMessageUtils.getMessage("label.point")
                        , facility != null? facility.getFacilityCd() : StringUtils.EMPTY
                        , CodedMessageUtils.getMessage("label.product")
                        , product != null? product.getProductCd() : StringUtils.EMPTY
                        , CodedMessageUtils.getMessage("label.productStockStatus")
                        , spStockStatusMap.containsKey(productStockStatusType)? spStockStatusMap.get(productStockStatusType).getCodeData1() : StringUtils.EMPTY
                        , CodedMessageUtils.getMessage("label.tableProductStockStatus") }));
            }

            productStockStsVO.setQuantity(productStockStsVO.getQuantity().subtract(minusQty));

            productStockStsRepo.save(BeanMapUtils.mapTo(productStockStsVO, ProductStockStatus.class));
        }
    }

    //beforeLocationIds将会查看是否取消主货位，afterLocationIds将会将该location设置为主货位
    public void doUpdateLocationMainFlag(Set<Long> beforeLocationIds,Set<Long> afterLocationIds) {

        //location当出现既要取消主货位，也要设置为主货位的情况则不更改
        Set<Long> intersection = beforeLocationIds.stream()
                .filter(afterLocationIds::contains)
                .collect(Collectors.toSet());

        // 从原集合中剔除交集部分
        beforeLocationIds.removeAll(intersection);
        afterLocationIds.removeAll(intersection);

        List<LocationVO> editList = new ArrayList<>();

        if (!ObjectUtils.isEmpty(beforeLocationIds) && !beforeLocationIds.isEmpty()) {

            List<LocationVO> beforeLocation =BeanMapUtils.mapListTo(locationRepo.findByLocationIdIn(beforeLocationIds),LocationVO.class);
            List<ProductInventoryVO> productInventorys = BeanMapUtils.mapListTo(productInventoryRepo.findByLocationIdInAndPrimaryFlag(beforeLocationIds,CommonConstants.CHAR_Y),ProductInventoryVO.class);
            Set<Long> productInventoryLocationIds = productInventorys.stream()
                                                                     .map(ProductInventoryVO::getLocationId)
                                                                     .collect(Collectors.toSet());

            beforeLocation.stream().filter(location -> !productInventoryLocationIds.contains(location.getLocationId()))
                                   .forEach(location -> location.setPrimaryFlag(CommonConstants.CHAR_N));

            editList.addAll(beforeLocation);
        }

        if (!ObjectUtils.isEmpty(afterLocationIds) && !afterLocationIds.isEmpty()) {

            List<LocationVO> afterLocation =BeanMapUtils.mapListTo(locationRepo.findByLocationIdIn(afterLocationIds),LocationVO.class);
            afterLocation.stream().forEach(location -> location.setPrimaryFlag(CommonConstants.CHAR_Y));

            editList.addAll(afterLocation);
        }

        if(!editList.isEmpty()){

            locationRepo.saveInBatch(BeanMapUtils.mapListTo(editList,Location.class));
        }
    }


    /**
     *
     * @param siteId
     * @param facilityId
     * @param productId
     * @param productStockStatusType
     * @param plusQty
     */
    public void doUpdateProductStockStatusPlusQty(String siteId
                                                , Long facilityId
                                                , Long productId
                                                , String productStockStatusType
                                                , BigDecimal plusQty) {

        plusQty = plusQty != null? plusQty : BigDecimal.ZERO;
        ProductStockStatusVO productStockStsVO = BeanMapUtils.mapTo(productStockStsRepo.findProductStockStatus(siteId, facilityId, productId, productStockStatusType), ProductStockStatusVO.class);

        if (productStockStsVO == null) {
            productStockStsVO = new ProductStockStatusVO();

            productStockStsVO.setSiteId(siteId);
            productStockStsVO.setFacilityId(facilityId);
            productStockStsVO.setProductId(productId);
            productStockStsVO.setProductStockStatusType(productStockStatusType);
            productStockStsVO.setQuantity(BigDecimal.ZERO);
            productStockStsVO.setProductClassification(ProductClsType.PART.getCodeDbid());
        }
        productStockStsVO.setQuantity(productStockStsVO.getQuantity().add(plusQty));

        productStockStsRepo.save(BeanMapUtils.mapTo(productStockStsVO, ProductStockStatus.class));
    }

    /**
     *
     * @param siteId
     * @param facilityId
     * @param productId
     * @param targetQty
     * @param targetStockStatus
     * @param stockStatusVOChangeMap
     */
    public void generateStockStatusVOMap(String siteId
                                        , Long facilityId
                                        , Long productId
                                        , BigDecimal targetQty
                                        , String targetStockStatus
                                        , Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap) {

        Map<Long, ProductStockStatusVO> valueMap = stockStatusVOChangeMap.containsKey(targetStockStatus) ? stockStatusVOChangeMap.get(targetStockStatus) : new HashMap<>();

        ProductStockStatusVO stockStatusVO = valueMap.get(productId);

        if (stockStatusVO == null){

            stockStatusVO = new ProductStockStatusVO();

            stockStatusVO.setSiteId(siteId);
            stockStatusVO.setProductId(productId);
            stockStatusVO.setFacilityId(facilityId);
            stockStatusVO.setQuantity(targetQty);
            stockStatusVO.setProductStockStatusType(targetStockStatus);
            stockStatusVO.setProductClassification(ProductClsType.PART.getCodeDbid());
            stockStatusVO.setUpdateProgram(ThreadLocalPJAuditableDetailAccessor.getValue().getUpdateProgram());
            stockStatusVO.setLastUpdatedBy(ThreadLocalPJAuditableDetailAccessor.getValue().getUpdateProgram());
        } else {

            stockStatusVO.setLastUpdatedBy(ThreadLocalPJAuditableDetailAccessor.getValue().getUpdateProgram());
            stockStatusVO.setQuantity(stockStatusVO.getQuantity().add(targetQty));
            stockStatusVO.setLastUpdatedBy(ThreadLocalPJAuditableDetailAccessor.getValue().getUpdateProgram());
        }

        valueMap.put(productId, stockStatusVO);
        stockStatusVOChangeMap.put(targetStockStatus, valueMap);
    }

    /**
     * 生成库存交易VO
     *
     * @param siteId 站点ID
     * @param inOutType 收支类型
     * @param targetPointId 目标点ID
     * @param fromPointId 来源点ID
     * @param toPointId 目的点ID
     * @param productId 产品ID
     * @param productCd 产品代码
     * @param productNm 产品名称
     * @param transactionType 交易类型
     * @param receiptQty 收货数量
     * @param stockQty 库存数量
     * @param receiptPrice 收货价格
     * @param receiptSlipId 收货单ID
     * @param receiptSlipNo 收货单单号
     * @param fromOrganizationId 来源组织ID
     * @param toOrganizationId 目标组织ID
     * @param locationId 位置ID
     * @param productCostVO 产品成本VO
     * @param reasonType 原因类型
     * @param personId 人员ID
     * @param personNm 人员姓名
     * @param productClassification 产品分类
     * @return InventoryTransactionVO 库存交易VO
     */
    public InventoryTransactionVO generateInventoryTransactionVO(String siteId
                                                               , String inOutType
                                                               , Long targetPointId
                                                               , Long fromPointId
                                                               , Long toPointId
                                                               , Long productId
                                                               , String productCd
                                                               , String productNm
                                                               , String transactionType
                                                               , BigDecimal receiptQty
                                                               , BigDecimal stockQty
                                                               , BigDecimal receiptPrice
                                                               , Long receiptSlipId
                                                               , String receiptSlipNo
                                                               , Long fromOrganizationId
                                                               , Long toOrganizationId
                                                               , Long locationId
                                                               , ProductCostVO productCostVO
                                                               , String reasonType
                                                               , Long personId
                                                               , String personNm
                                                               , String productClassification) {

        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);
        String sysTime = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M_S);
        Map<String, ConstantsBO> inventoryTransMap = constantsLogic.getConstantsMap(InventoryTransactionType.class.getDeclaredFields());
        Map<String, ConstantsBO> stockAdjustReasonMap = constantsLogic.getConstantsMap(StockAdjustReasonType.class.getDeclaredFields());

        InventoryTransactionVO invTranVO = new InventoryTransactionVO();

        invTranVO.setSiteId(siteId);
        invTranVO.setPhysicalTransactionDate(sysDate);
        invTranVO.setPhysicalTransactionTime(sysTime);
        invTranVO.setFromFacilityId(fromPointId);
        invTranVO.setFromOrganizationId(fromOrganizationId);
        invTranVO.setToOrganizationId(toOrganizationId);
        invTranVO.setToFacilityId(toPointId);
        invTranVO.setProductId(productId);
        invTranVO.setProductCd(productCd);
        invTranVO.setProductNm(productNm);
        invTranVO.setTargetFacilityId(targetPointId);
        invTranVO.setRelatedSlipId(receiptSlipId);
        invTranVO.setRelatedSlipNo(receiptSlipNo);
        invTranVO.setInventoryTransactionType(transactionType);
        String invTransNm = inventoryTransMap.containsKey(transactionType)? inventoryTransMap.get(transactionType).getCodeData1() : StringUtils.EMPTY;
        invTranVO.setInventoryTransactionNm(invTransNm);
        if (StringUtils.equals(inOutType, InOutType.IN)) {
            invTranVO.setInQty(receiptQty);
            invTranVO.setCurrentQty(stockQty.add(receiptQty));
            invTranVO.setInCost(receiptPrice);
        }
        if (StringUtils.equals(inOutType, InOutType.OUT)) {
            invTranVO.setOutQty(receiptQty);
            invTranVO.setCurrentQty(stockQty.subtract(receiptQty));
            invTranVO.setOutCost(receiptPrice);
        }
//        ProductCostVO productCostVO = BeanMapUtils.mapTo(productCostRepo.findByProductIdAndCostTypeAndSiteId(productId, CostType.AVERAGE_COST, siteId), ProductCostVO.class);
        invTranVO.setCurrentAverageCost(productCostVO != null? productCostVO.getCost() : null);
        invTranVO.setStockAdjustmentReasonType(reasonType);
        String reasonTypeNm = stockAdjustReasonMap.containsKey(reasonType)? stockAdjustReasonMap.get(reasonType).getCodeData1() : StringUtils.EMPTY;
        invTranVO.setStockAdjustmentReasonNm(reasonTypeNm);
        invTranVO.setLocationId(locationId);
        LocationVO location = BeanMapUtils.mapTo(locationRepo.findByLocationId(locationId), LocationVO.class);
        invTranVO.setLocationCd(location != null? location.getLocationCd() : StringUtils.EMPTY);
        invTranVO.setProductClassification(productClassification);
        invTranVO.setReporterId(personId);
        invTranVO.setReporterNm(personNm);

        return invTranVO;
    }

    /**
     * For update productStockStatus/inventoryTransaction/productCost
     * @param receiptSlipItemVOs
     * @param targetReceiptPointId
     * @param inventoryTransactionType
     */
    public void doInventoryReceipt(List<ReceiptSlipItemVO> receiptSlipItemVOs, Long targetReceiptPointId, String inventoryTransactionType, Long personId, String personNm) {

        if (receiptSlipItemVOs.isEmpty()) {
            throw new BusinessCodedException("no receiptSlipItemVOs exist");
        }
        List<ProductStockStatusVO> updProductStockStsList = new ArrayList<>();
        List<InventoryTransactionVO> saveInventoryTransList = new ArrayList<>();

        String siteId = receiptSlipItemVOs.get(0).getSiteId();
        Set<Long> productIds = receiptSlipItemVOs.stream().map(ReceiptSlipItemVO::getProductId).collect(Collectors.toSet());
        Set<String> productStockStatusTypes = paramStockStatusTypeIn();

        List<ProductStockStatus> stockStatusList = productStockStsRepo.findStockStatusList(siteId, targetReceiptPointId, productIds, ProductClsType.PART.getCodeDbid(), productStockStatusTypes);
        List<ProductStockStatusVO> stockStatusVOList = BeanMapUtils.mapListTo(stockStatusList, ProductStockStatusVO.class);

        // 取得所有productStockStatusVO.quantity之和为stockQty
        Map<Long, BigDecimal> productStockQtyMap = new HashMap<>();
        Map<Long, List<ProductStockStatusVO>> stockStatusGroupByProduct = stockStatusVOList.stream().collect(Collectors.groupingBy(ProductStockStatusVO::getProductId));
        for (Long productId : stockStatusGroupByProduct.keySet()) {
            List<ProductStockStatusVO> stockStatusListByProduct = stockStatusGroupByProduct.get(productId);

            BigDecimal stockQty = stockStatusListByProduct.stream().map(item -> item.getQuantity() != null ? item.getQuantity() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            productStockQtyMap.put(productId, stockQty);
        }

        // 构建Map<String, Map<Long, ProductStockStatusVO>>
        Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();
        buildProductStockStatusVOMap(stockStatusVOList, stockStatusVOChangeMap);
        // 根据receiptSlipItemVOs.getReceiptSlipVOs
        Set<Long> receiptSlipIds = receiptSlipItemVOs.stream().map(ReceiptSlipItemVO::getReceiptSlipId).collect(Collectors.toSet());
        List<ReceiptSlipVO> receiptSlipVOList = BeanMapUtils.mapListTo(receiptSlipRepo.findByReceiptSlipIdIn(receiptSlipIds), ReceiptSlipVO.class);
        Map<Long, ReceiptSlipVO> receiptSlipMap = receiptSlipVOList.stream().collect(Collectors.toMap(ReceiptSlipVO::getReceiptSlipId, Function.identity()));

        for (ReceiptSlipItemVO item : receiptSlipItemVOs) {
            if (item.getFrozenQty().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal frozenQty = item.getFrozenQty() != null? item.getFrozenQty() : BigDecimal.ZERO;
                generateStockStatusVOMap(siteId
                                        , targetReceiptPointId
                                        , item.getProductId()
                                        , item.getReceiptQty().subtract(frozenQty)
                                        , SpStockStatus.ONRECEIVING_QTY.getCodeDbid()
                                        , stockStatusVOChangeMap);

                generateStockStatusVOMap(siteId
                                        , targetReceiptPointId
                                        , item.getProductId()
                                        , item.getFrozenQty()
                                        , SpStockStatus.ONFROZEN_QTY.getCodeDbid()
                                        , stockStatusVOChangeMap);
            } else {
                generateStockStatusVOMap(siteId
                                        , targetReceiptPointId
                                        , item.getProductId()
                                        , item.getReceiptQty()
                                        , SpStockStatus.ONRECEIVING_QTY.getCodeDbid()
                                        , stockStatusVOChangeMap);
            }

            // 生成 productCostVOs
            ProductCostVO productCostVO = costMgr.doCostCalculation(siteId, item.getProductId(), item.getReceiptPrice(), item.getReceiptQty(), BigDecimal.ZERO, targetReceiptPointId);

            // 生成inventoryTransactionVOs
            ReceiptSlipVO receiptSlipVO = receiptSlipMap.containsKey(item.getReceiptSlipId())? receiptSlipMap.get(item.getReceiptSlipId()) : null;
            String slipNo = receiptSlipVO != null? receiptSlipVO.getSlipNo() : StringUtils.EMPTY;
            Long fromOrgId = receiptSlipVO != null? receiptSlipVO.getFromOrganizationId() : null;
            BigDecimal stockQty = productStockQtyMap.containsKey(item.getProductId())? productStockQtyMap.get(item.getProductId()) : BigDecimal.ZERO;
            InventoryTransactionVO invTranVO = generateInventoryTransactionVO(siteId, InOutType.IN, targetReceiptPointId, null, targetReceiptPointId
                                                                            , item.getProductId(), item.getProductCd(), item.getProductNm()
                                                                            , inventoryTransactionType
                                                                            , item.getReceiptQty(), stockQty, item.getReceiptPrice()
                                                                            , item.getReceiptSlipId(), slipNo
                                                                            , fromOrgId
                                                                            , receiptSlipVO.getReceivedOrganizationId()
                                                                            , null
                                                                            , productCostVO
                                                                            , StringUtils.EMPTY
                                                                            , personId
                                                                            , personNm
                                                                            , ProductClsType.PART.getCodeDbid());


            saveInventoryTransList.add(invTranVO);
        }

        this.updateProductStockStatusByMap(stockStatusVOChangeMap);
        inventoryTransRepo.saveInBatch(BeanMapUtils.mapListTo(saveInventoryTransList, InventoryTransaction.class));
    }

    public void doSalesReturn(ReceiptSlipVO receiptSlipVO, Long personId, String personNm) {

        if (receiptSlipVO == null) {
            throw new BusinessCodedException("no ReceiptSlip exist");
        }
        List<ReceiptSlipItemVO> receiptSlipItemList = BeanMapUtils.mapListTo(receiptSlipItemRepo.findByReceiptSlipId(receiptSlipVO.getReceiptSlipId()), ReceiptSlipItemVO.class);
        doInventoryReceipt(receiptSlipItemList, receiptSlipVO.getReceivedFacilityId(), InventoryTransactionType.RETURNIN.getCodeDbid(), personId, personNm);
    }

    /**
    *
    * @param currentSiteId
    * @param partsId
    * @param pickingFacilityId
    * @param requestQuantity
    * @param locationMap
    * @return
    */
   public List<PickingResultItemBO> doPickingInstruction(String currentSiteId, Long partsId, Long pickingFacilityId, BigDecimal requestQuantity, Map<Long, LocationVO> locationMap, String pickingFacilityCd, String partsCd) {

       List<PickingResultItemBO> resultItems = new ArrayList<>();

       //1. Find the qualify locations
       List<ProductInventoryVO> pis = BeanMapUtils.mapListTo(productInventoryRepo.findProductInventorys(currentSiteId, pickingFacilityId, partsId), ProductInventoryVO.class);
       BigDecimal remainQty = requestQuantity;
       List<ProductInventoryVO>  normalLocationNoMainInvs = new ArrayList<>();
       ProductInventoryVO mainLocationTypeInv = null;

       for (ProductInventoryVO proInv : pis) {
           BigDecimal invQty = proInv.getQuantity();
           if (invQty != null && invQty.compareTo(BigDecimal.ZERO) > 0) {
               if (locationMap.containsKey(proInv.getLocationId())) {
                   LocationVO loca = locationMap.get(proInv.getLocationId());
                   String locationClassify = loca.getLocationType();
                   if (StringUtils.equals(locationClassify, LocationType.TENTATIVE.getCodeDbid())) {  //Tentive type.
                       BigDecimal finalPickQty = (invQty.compareTo(remainQty)>=0 ? remainQty : invQty);
                       addPickingResultItemAndUpdateInventoryQty(resultItems, partsId, proInv, finalPickQty, locationMap);
                       remainQty = remainQty.subtract(finalPickQty); // remainQty-finalPickQty
                   } else if (StringUtils.equals(locationClassify, LocationType.NORMAL.getCodeDbid())) {
                       if (isMainLocation(proInv)) { //Normal type
                           mainLocationTypeInv = proInv;
                       } else {
                           normalLocationNoMainInvs.add(proInv);
                       }
                   }
               }
           }
       }

       //Process normals
       if (remainQty.compareTo(BigDecimal.ZERO) > 0) {
           remainQty = processNormalLocationTypeInventorys(normalLocationNoMainInvs
                                                           , partsId
                                                           , mainLocationTypeInv
                                                           , remainQty
                                                           , resultItems
                                                           , locationMap);
       }
       if(requestQuantity!= null && requestQuantity.signum()== 1 && remainQty.signum()!= 0) {//requestQuantity is positive and pick is not complete, throw message!
           throw new BusinessCodedException("Location is not found, product code:" + partsCd + "; facility code:" + pickingFacilityCd);
       }

       //2. Update  stock status quantity
       if(resultItems.size() > 0) {
           updatePickedItemStockStatus(pickingFacilityId, partsId, currentSiteId, (remainQty.signum()==1 ? (requestQuantity.subtract(remainQty)) : requestQuantity));
       }

       return resultItems;
   }


   public void doStoringReport(List<StoringLineVO> storingLineVOs) {

       Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();
       List<ProductInventoryVO> productInventoryVOList = new ArrayList<>();

       //prepare parameter : siteId,facilityId,productIds,stockStatusType
       String siteId               = storingLineVOs.get(0).getSiteId();
       Long facilityId             = storingLineVOs.get(0).getFacilityId();
       Set<Long> productIds        = storingLineVOs.stream().map(StoringLineVO::getProductId).collect(Collectors.toSet());
       Set<String> stockStatusType = new HashSet<>(Arrays.asList(SpStockStatus.ONHAND_QTY.getCodeDbid(), SpStockStatus.ONFROZEN_QTY.getCodeDbid(), SpStockStatus.ONRECEIVING_QTY.getCodeDbid()));

       //map StockStatusVOChangeMap
       generateStockStatusVOChangeMap(stockStatusVOChangeMap, siteId, facilityId, productIds, stockStatusType);

       //get StoringLineItemVOs
       Set<Long> storingLineIds = storingLineVOs.stream().map(StoringLineVO::getStoringLineId).collect(Collectors.toSet());
       List<StoringLineItemVO> storingLineItemVOList = BeanMapUtils.mapListTo(storingLineItemRepo.findBySiteIdAndStoringLineIdIn(siteId, storingLineIds), StoringLineItemVO.class);
       Set<Long> locationIds = storingLineItemVOList.stream().map(StoringLineItemVO::getLocationId).collect(Collectors.toSet());

       List<ProductInventoryVO> productInventoryVOs = BeanMapUtils.mapListTo(productInventoryRepo.findProductInventoryList(siteId, productIds, facilityId, locationIds), ProductInventoryVO.class);

       //productInventoryVOMap <productId+'|'+locationId , ProductInventoryVO>
       Map<String, ProductInventoryVO> productInventoryVOMap = productInventoryVOs.stream().collect(Collectors.toMap(
                                                                                   v -> v.getProductId().toString() + CommonConstants.CHAR_VERTICAL_BAR + v.getLocationId().toString(),
                                                                                   Function.identity()));

       Map<Long, StoringLineVO> storingLineVOMap = storingLineVOs.stream().collect(Collectors.toMap(StoringLineVO::getStoringLineId, Function.identity()));

       for (StoringLineItemVO storingLineItemVO : storingLineItemVOList) {

           Long productId = storingLineVOMap.get(storingLineItemVO.getStoringLineId()).getProductId();

           ProductInventoryVO productInventoryVOUpdate = generateProductInventoryByCondition(siteId
                                                                                           , storingLineItemVO.getFacilityId()
                                                                                           , productId
                                                                                           , storingLineItemVO.getLocationId()
                                                                                           , storingLineItemVO.getStoredQty()
                                                                                           , productInventoryVOMap.get(productId.toString() + CommonConstants.CHAR_VERTICAL_BAR + storingLineItemVO.getLocationId().toString())
                                                                                           , CommonConstants.CHAR_PLUS);
           productInventoryVOList.add(productInventoryVOUpdate);
       }

       for (StoringLineVO storingLineVO : storingLineVOs) {

           generateStockStatusVOMap(siteId
                                  , facilityId
                                  , storingLineVO.getProductId()
                                  , storingLineVO.getStoredQty().negate()
                                  , SpStockStatus.ONRECEIVING_QTY.getCodeDbid()
                                  , stockStatusVOChangeMap);

           generateStockStatusVOMap(siteId
                                  , facilityId
                                  , storingLineVO.getProductId()
                                  , storingLineVO.getStoredQty()
                                  , SpStockStatus.ONHAND_QTY.getCodeDbid()
                                  , stockStatusVOChangeMap);

           if (BigDecimal.ZERO.compareTo(storingLineVO.getFrozenQty()) < 0) {

               generateStockStatusVOMap(siteId
                                      , facilityId
                                      , storingLineVO.getProductId()
                                      , storingLineVO.getFrozenQty()
                                      , SpStockStatus.ONFROZEN_QTY.getCodeDbid()
                                      , stockStatusVOChangeMap);
           }
       }

       // save productStockStatusVOs
       List<ProductStockStatusVO> updProductStockStsList = new ArrayList<>();
       for (String type : stockStatusVOChangeMap.keySet()) {

           Map<Long, ProductStockStatusVO> productStockStsMap = stockStatusVOChangeMap.get(type);
           for (Long productId : productStockStsMap.keySet()) {

               ProductStockStatusVO productStockSts = productStockStsMap.get(productId);
               updProductStockStsList.add(productStockSts);
           }
       }
       productStockStsRepo.saveInBatch(BeanMapUtils.mapListTo(updProductStockStsList, ProductStockStatus.class));
       productInventoryRepo.saveInBatch(BeanMapUtils.mapListTo(productInventoryVOList, ProductInventory.class));
   }

   private ProductInventoryVO generateProductInventoryByCondition(String siteId
                                                                , Long facilityId
                                                                , Long productId
                                                                , Long locationId
                                                                , BigDecimal changeQty
                                                                , ProductInventoryVO productInventoryVO
                                                                , String changeType) {

       if (productInventoryVO == null) {

           if (CommonConstants.CHAR_PLUS.equals(changeType)) {

               ProductInventoryVO productInventoryVONew = new ProductInventoryVO();

               productInventoryVONew.setSiteId(siteId);
               productInventoryVONew.setFacilityId(facilityId);
               productInventoryVONew.setProductId(productId);
               productInventoryVONew.setLocationId(locationId);
               productInventoryVONew.setPrimaryFlag(CommonConstants.CHAR_N);
               productInventoryVONew.setQuantity(changeQty);
               productInventoryVONew.setProductClassification(ProductClsType.PART.getCodeDbid());

               return productInventoryVONew;

           }else if (CommonConstants.CHAR_MINUS.equals(changeType)) {

               //error
               throw new BusinessCodedException("productInventoryVO is null cannot do minus");//need confirm the error message
           }
       } else {

           if (CommonConstants.CHAR_MINUS.equals(changeType) && BigDecimal.ZERO.compareTo(productInventoryVO.getQuantity().add(changeQty)) > 0) {

               //error
               throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00467", new String[] {productInventoryVO.getProductId().toString()}));
           }else {

               //update
               productInventoryVO.setQuantity(changeQty);
               return productInventoryVO;
           }
       }
       return productInventoryVO;
   }

   public ProductInventoryVO doUpdateProductInventoryQtyByCondition(String siteId
                                                                   , Long facilityId
                                                                   , Long productId
                                                                   , Long locationId
                                                                   , BigDecimal changeQty
                                                                   , ProductInventoryVO productInventoryVO
                                                                   , String changeType) {

       MstProduct product = productRepo.findByProductId(productId);
       if (productInventoryVO == null) {

           if (CommonConstants.CHAR_PLUS.equals(changeType)) {

               //insert
               insertProductInventoryVO(siteId
                                      , facilityId
                                      , productId
                                      , locationId
                                      , CommonConstants.CHAR_N
                                      , changeQty
                                      , ProductClsType.PART.getCodeDbid());

           }else if (CommonConstants.CHAR_MINUS.equals(changeType)) {

               MstFacility facility = facilityRepo.findByFacilityId(facilityId);

               Map<String, ConstantsBO> spStockStatusMap = constantsLogic.getConstantsMap(SpStockStatus.class.getDeclaredFields());

               throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00316", new String[]{
                                                                             CodedMessageUtils.getMessage("label.point")
                                                                           , facility != null? facility.getFacilityCd() : StringUtils.EMPTY
                                                                           , CodedMessageUtils.getMessage("label.product")
                                                                           , product != null? product.getProductCd() : StringUtils.EMPTY
                                                                           , CodedMessageUtils.getMessage("label.location")
                                                                           , locationId.toString()
                                                                           , CodedMessageUtils.getMessage("label.tableProductInventory") }));
           }
       } else {

           if (CommonConstants.CHAR_MINUS.equals(changeType) && BigDecimal.ZERO.compareTo(productInventoryVO.getQuantity().add(changeQty)) > 0) {

               throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00467", new String[] {product != null? product.getProductCd() : StringUtils.EMPTY}));
           }else {

               if(CommonConstants.CHAR_MINUS.equals(changeType)) {
                 //库存减
                   productInventoryVO.setQuantity(productInventoryVO.getQuantity().subtract(changeQty));
               }else {
                 //库存加
                   productInventoryVO.setQuantity(productInventoryVO.getQuantity().add(changeQty));
               }

               productInventoryRepo.save(BeanMapUtils.mapTo(productInventoryVO, ProductInventory.class));
           }
       }
       return productInventoryVO;
   }

   public void updateMainLocationOfProductInventory(Long locationId
                                                  , String siteId
                                                  , Long productId
                                                  , Long facilityId
                                                  , String mainLocationCd
                                                  , ProductInventoryVO productInventoryVO) {

       if (productInventoryVO == null) {
           throw new BusinessCodedException("no productInventoryVO exist");
       }

       if (StringUtil.isNotBlank(mainLocationCd)) {

           List<ProductInventoryVO> productInvVOList = BeanMapUtils.mapListTo(productInventoryRepo.findByFacilityIdAndProductIdAndSiteIdAndPrimaryFlag(facilityId, productId, siteId, CommonConstants.CHAR_Y), ProductInventoryVO.class);

           if (productInvVOList == null || productInvVOList.isEmpty()) {

               insertProductInventoryVO(siteId
                                      , facilityId
                                      , productId
                                      , locationId
                                      , CommonConstants.CHAR_Y
                                      , BigDecimal.ZERO
                                      , null);
           }else {

               ProductInventoryVO productInvVO = productInvVOList.get(0);//when primaryKey=Y,can get only one productInventory
               if (!Objects.equals(productInvVO.getLocationId(), locationId)) {

                   productInvVO.setPrimaryFlag(CommonConstants.CHAR_N);
                   productInventoryRepo.save(BeanMapUtils.mapTo(productInvVO, ProductInventory.class));

                   ProductInventoryVO productInventoryVOUpdate = BeanMapUtils.mapTo(productInventoryRepo.findByFacilityIdAndProductIdAndSiteIdAndLocationId(facilityId, productId, siteId, locationId), ProductInventoryVO.class);

                   if (productInventoryVOUpdate == null) {

                       insertProductInventoryVO(siteId
                                              , facilityId
                                              , productId
                                              , locationId
                                              , CommonConstants.CHAR_Y
                                              , BigDecimal.ZERO
                                              , null);
                   }else {

                       productInventoryVOUpdate.setPrimaryFlag(CommonConstants.CHAR_Y);
                       productInventoryRepo.save(BeanMapUtils.mapTo(productInventoryVOUpdate, ProductInventory.class));
                   }
               }
           }
       }else {

           if (BigDecimal.ZERO.compareTo(productInventoryVO.getQuantity()) == 0) {

               productInventoryRepo.delete(BeanMapUtils.mapTo(productInventoryVO, ProductInventory.class));
           }else {

               productInventoryVO.setPrimaryFlag(CommonConstants.CHAR_N);
               productInventoryRepo.save(BeanMapUtils.mapTo(productInventoryVO, ProductInventory.class));
           }
       }
   }

   /**
    * For insert or update productInventory(当需要设置productInventoryVO为主库位时进行调用)
    */
    public void updateMainLocationOfProdInventory(Long locationId
                                                   , String siteId
                                                   , Long productId
                                                   , Long facilityId
                                                   , BigDecimal qty
                                                   , ProductInventoryVO productInventoryVO) {

        //查找原主库位数据
        ProductInventoryVO productInventoryVOMain = BeanMapUtils.mapTo(productInventoryRepo.findFirstByFacilityIdAndProductIdAndSiteIdAndPrimaryFlag(facilityId,
                                                                                                                                                     productId,
                                                                                                                                                     siteId,
                                                                                                                                                     CommonConstants.CHAR_Y), ProductInventoryVO.class);
        Set<Long> beforeLocationIds = new HashSet<>();
        Set<Long> afterLocationIds =new HashSet<>();
        //productInventoryVO <> null (实际上不存在productInventoryVO不存在的例子，SPM0305页面Confirm中会先新建productInventoryVO至数据库中)
        if (!Nulls.isNull(productInventoryVO)) {

            //若原主库位数据存在, 则更新productInventoryVO.setPrimaryFlag = Y
            if (!Nulls.isNull(productInventoryVOMain)) {

                //productInventoryVOMain.quantity = 0, 删除productInventoryVOMain
                if (NumberUtil.equals(productInventoryVOMain.getQuantity(), BigDecimal.ZERO)) {

                    productInventoryRepo.delete(BeanMapUtils.mapTo(productInventoryVOMain, ProductInventory.class));

                } else {

                    //productInventoryVOMain.quantity <> 0, 原主库位取消
                    productInventoryVOMain.setPrimaryFlag(CommonConstants.CHAR_N);
                    productInventoryRepo.save(BeanMapUtils.mapTo(productInventoryVOMain, ProductInventory.class));
                    beforeLocationIds.add(productInventoryVOMain.getLocationId());
                }

                //设置新主库位
                productInventoryVO.setPrimaryFlag(CommonConstants.CHAR_Y);
                productInventoryVO.setQuantity(NumberUtil.add(productInventoryVO.getQuantity(), qty));
                afterLocationIds.add(productInventoryVO.getLocationId());
                productInventoryRepo.save(BeanMapUtils.mapTo(productInventoryVO, ProductInventory.class));
                doUpdateLocationMainFlag(beforeLocationIds, afterLocationIds);

            } else {

                //若原主库位数据不存在, 更新productInventoryVO.setPrimaryFlag = Y
                productInventoryVO.setPrimaryFlag(CommonConstants.CHAR_Y);
                productInventoryRepo.save(BeanMapUtils.mapTo(productInventoryVO, ProductInventory.class));
                afterLocationIds.add(productInventoryVO.getLocationId());
                doUpdateLocationMainFlag(new HashSet<>(), afterLocationIds);
            }

        } else {

            //若productInventoryVO不存在则新增一条主库位
            ProductInventoryVO newProductInventoryVO = new ProductInventoryVO();
            newProductInventoryVO.setSiteId(siteId);
            newProductInventoryVO.setFacilityId(facilityId);
            newProductInventoryVO.setProductId(productId);
            newProductInventoryVO.setLocationId(locationId);
            newProductInventoryVO.setPrimaryFlag(CommonConstants.CHAR_Y);
            productInventoryRepo.save(BeanMapUtils.mapTo(newProductInventoryVO, ProductInventory.class));

        }
   }

   public void doShippingRequest(List<DeliveryOrderItemVO> deliveryOrderItemVOs) {

       if (deliveryOrderItemVOs == null || deliveryOrderItemVOs.isEmpty()) {
           throw new BusinessCodedException("no deliveryOrderItemVOs exist");
       }

       Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();

       // 准备构建stockStatusVOChangeMap需要的参数
       DeliveryOrderVO deliveryOrderVO     = BeanMapUtils.mapTo(deliveryOrderRepo.findByDeliveryOrderId(deliveryOrderItemVOs.get(0).getDeliveryOrderId()), DeliveryOrderVO.class);
       Long facilityId                     = deliveryOrderVO.getFromFacilityId();
       String siteId                       = deliveryOrderItemVOs.get(0).getSiteId();

       for (DeliveryOrderItemVO item : deliveryOrderItemVOs) {

           generateStockStatusVOMap(siteId, facilityId, item.getProductId(), item.getDeliveryQty().negate(), SpStockStatus.ONHAND_QTY.getCodeDbid(), stockStatusVOChangeMap);

           generateStockStatusVOMap(siteId, facilityId, item.getProductId(), item.getDeliveryQty(), SpStockStatus.ALLOCATED_QTY.getCodeDbid(), stockStatusVOChangeMap);
       }

       // save productStockStatusVOs
       this.updateProductStockStatusByMap(stockStatusVOChangeMap);

   }

   public void doShippingCompletion(List<DeliveryOrderItemVO> deliveryOrderItemVOs, String inventoryTransactionType, Long personId, String personNm) {

       if(deliveryOrderItemVOs == null || deliveryOrderItemVOs.isEmpty()) {
           throw new BusinessCodedException("no deliveryOrderItemVOs exist");
       }

       Map<Long, BigDecimal> productStockQtyMap                            = new HashMap<>();
       Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();
       List<InventoryTransactionVO> saveInventoryTransList                 = new ArrayList<>();

       //prepare parameters : facilityId, siteId, productIds, productStockStatusTypes
       DeliveryOrderVO deliveryOrderVO = BeanMapUtils.mapTo(deliveryOrderRepo.findByDeliveryOrderId(deliveryOrderItemVOs.get(0).getDeliveryOrderId()), DeliveryOrderVO.class);
       Long facilityId                 = deliveryOrderVO.getFromFacilityId();
       Long toFacilityId               = deliveryOrderVO.getToFacilityId();
       String siteId                   = deliveryOrderItemVOs.get(0).getSiteId();
       Set<Long> productIds            = deliveryOrderItemVOs.stream().map(DeliveryOrderItemVO::getProductId).collect(Collectors.toSet());
       Set<String> productStockStatusTypes = this.paramStockStatusTypeIn();

       // 取得所有productStockStatusVO.quantity之和为stockQty
       List<ProductStockStatusVO> stockStatusVOList = BeanMapUtils.mapListTo(productStockStsRepo.findStockStatusList(siteId, facilityId, productIds, ProductClsType.PART.getCodeDbid(), productStockStatusTypes), ProductStockStatusVO.class);
       Map<Long, List<ProductStockStatusVO>> stockStatusGroupByProduct = stockStatusVOList.stream().collect(Collectors.groupingBy(ProductStockStatusVO::getProductId));

       for (Long productId : stockStatusGroupByProduct.keySet()) {

           List<ProductStockStatusVO> stockStatusListByProduct = stockStatusGroupByProduct.get(productId);
           BigDecimal stockQty = stockStatusListByProduct.stream().map(item -> item.getQuantity() != null ? item.getQuantity() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);

           productStockQtyMap.put(productId, stockQty);
       }

       //获取productCostVOs(cost_type = S011RECEIVECOST)
       List<ProductCostVO> productCostVOList = BeanMapUtils.mapListTo(productCostRepo.findProductCostVOList(siteId, productIds, ProductClsType.PART.getCodeDbid(), CostType.RECEIVE_COST), ProductCostVO.class);
       Map<Long, ProductCostVO> productCostVOMap = productCostVOList.stream().collect(Collectors.toMap(ProductCostVO::getProductId, Function.identity()));

       // 构建stockStatusVOChangeMap
       if(InventoryTransactionType.SALESTOCKOUT.getCodeDbid().equals(inventoryTransactionType)) {// S027SALESTOCKOUT

           for (DeliveryOrderItemVO item : deliveryOrderItemVOs) {

               this.generateStockStatusVOMap(siteId
                                           , facilityId
                                           , item.getProductId()
                                           , item.getDeliveryQty().negate()
                                           , SpStockStatus.ONPICKING_QTY.getCodeDbid()
                                           , stockStatusVOChangeMap);

           }
       }else if(InventoryTransactionType.EXPENSESALESTOCKOUT.getCodeDbid().equals(inventoryTransactionType)) {// S027EXPENSESALESTOCKOUT

           for (DeliveryOrderItemVO item : deliveryOrderItemVOs) {

               this.generateStockStatusVOMap(siteId
                                           , facilityId
                                           , item.getProductId()
                                           , item.getDeliveryQty().negate()
                                           , SpStockStatus.ONPICKING_QTY.getCodeDbid()
                                           , stockStatusVOChangeMap);
           }
       }else if(InventoryTransactionType.BORROWINGRELEASE.getCodeDbid().equals(inventoryTransactionType)) {// S027BORROWINGRELEASE

           for (DeliveryOrderItemVO item : deliveryOrderItemVOs) {

               this.generateStockStatusVOMap(siteId
                                           , facilityId
                                           , item.getProductId()
                                           , item.getDeliveryQty().negate()
                                           , SpStockStatus.ONBORROWING_QTY.getCodeDbid()
                                           , stockStatusVOChangeMap);

               this.generateStockStatusVOMap(siteId
                                           , facilityId
                                           , item.getProductId()
                                           , item.getDeliveryQty()
                                           , SpStockStatus.ONDELIVERY_QTY.getCodeDbid()
                                           , stockStatusVOChangeMap);
           }
       }else if(InventoryTransactionType.TRANSFEROUT.getCodeDbid().equals(inventoryTransactionType)) {// S027TRANSFEROUT

           //from
           Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeToMap = new HashMap<>();

           for (DeliveryOrderItemVO item : deliveryOrderItemVOs) {

               // fromPoint OnPickingQty minus
               this.generateStockStatusVOMap(siteId
                                           , facilityId
                                           , item.getProductId()
                                           , item.getDeliveryQty().negate()
                                           , SpStockStatus.ONPICKING_QTY.getCodeDbid()
                                           , stockStatusVOChangeMap);
               // fromPoint onTransferOutQty plus
               this.generateStockStatusVOMap(siteId
                                           , facilityId
                                           , item.getProductId()
                                           , item.getDeliveryQty()
                                           , SpStockStatus.ONTRANSFER_OUT_QTY.getCodeDbid()
                                           , stockStatusVOChangeMap);
               // toPoint onTransferInQty plus
               this.generateStockStatusVOMap(siteId
                                           , toFacilityId
                                           , item.getProductId()
                                           , item.getDeliveryQty()
                                           , SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid()
                                           , stockStatusVOChangeToMap);

               stockStatusVOChangeMap.putAll(stockStatusVOChangeToMap);
           }
       }else if(InventoryTransactionType.RETURNIN.getCodeDbid().equals(inventoryTransactionType)) {// S027EXPENSESALESTOCKOUT

        for (DeliveryOrderItemVO item : deliveryOrderItemVOs) {

            this.generateStockStatusVOMap(siteId
                                        , facilityId
                                        , item.getProductId()
                                        , item.getDeliveryQty().negate()
                                        , SpStockStatus.ONDELIVERY_QTY.getCodeDbid()
                                        , stockStatusVOChangeMap);
        }
    }

       //make deliveryOrderVOMap
       Set<Long> deliveryOrderIds                    = deliveryOrderItemVOs.stream().map(DeliveryOrderItemVO::getDeliveryOrderId).collect(Collectors.toSet());
       List<Long> deliveryOrderIdList                = new ArrayList<>(deliveryOrderIds);
       List<DeliveryOrderVO> deliveryOrderVOList     = BeanMapUtils.mapListTo(deliveryOrderRepo.findByDeliveryOrderIdIn(deliveryOrderIdList), DeliveryOrderVO.class);
       Map<Long, DeliveryOrderVO> deliveryOrderVOMap = deliveryOrderVOList.stream().collect(Collectors.toMap(DeliveryOrderVO::getDeliveryOrderId, Function.identity()));

       // 生成inventoryTransactionVOs
       for (DeliveryOrderItemVO item : deliveryOrderItemVOs) {

           InventoryTransactionVO invTranVO = generateInventoryTransactionVO(siteId
                                                                           , InOutType.OUT
                                                                           , toFacilityId
                                                                           , deliveryOrderVOMap.get(item.getDeliveryOrderId()).getFromFacilityId()
                                                                           , deliveryOrderVOMap.get(item.getDeliveryOrderId()).getToFacilityId()
                                                                           , item.getProductId()
                                                                           , item.getProductCd()
                                                                           , item.getProductNm()
                                                                           , inventoryTransactionType
                                                                           , item.getDeliveryQty()
                                                                           , productStockQtyMap.get(item.getProductId())
                                                                           , item.getProductCost()
                                                                           , deliveryOrderVOMap.get(item.getDeliveryOrderId()).getDeliveryOrderId()
                                                                           , deliveryOrderVOMap.get(item.getDeliveryOrderId()).getDeliveryOrderNo()
                                                                           , deliveryOrderVOMap.get(item.getDeliveryOrderId()).getFromOrganizationId()
                                                                           , deliveryOrderVOMap.get(item.getDeliveryOrderId()).getToOrganizationId()
                                                                           , null
                                                                           , productCostVOMap.get(item.getProductId())
                                                                           , CommonConstants.CHAR_BLANK
                                                                           , personId
                                                                           , personNm
                                                                           , ProductClsType.PART.getCodeDbid());
           saveInventoryTransList.add(invTranVO);
       }

       this.updateProductStockStatusByMap(stockStatusVOChangeMap);
       inventoryTransRepo.saveInBatch(BeanMapUtils.mapListTo(saveInventoryTransList, InventoryTransaction.class));
   }

   public void doStockAdjustMinus(String siteId
                               , Long pointId
                               , String adjustType
                               , Long fromLocationId
                               , Long toLocationId
                               , Long productId
                               , String reasonId
                               , BigDecimal adjustedQty
                               , Long personId
                               , String personNm) {

       doStockAdjust(siteId, pointId, adjustType, fromLocationId, toLocationId, productId, reasonId, adjustedQty, null, MINUS,personId,personNm);
   }

   public void doStockAdjustPlus(String siteId
                               , Long pointId
                               , String adjustType
                               , Long fromLocationId
                               , Long toLocationId
                               , Long productId
                               , String reasonId
                               , BigDecimal adjustedQty
                               , BigDecimal productCost
                               , Long personId
                               , String personNm) {

       doStockAdjust(siteId, pointId, adjustType, fromLocationId, toLocationId, productId, reasonId, adjustedQty, productCost, PLUS,personId,personNm);
   }

   public void doLocationStockMovement(String siteId
                                       , Long productId
                                       , Long stockPointId
                                       , Long fromLocationId
                                       , Long toLocationId
                                       , BigDecimal targetQty
                                       , Long picId) {

       String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);
       String sysTime = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M);

       String locTransNo = generateNoMgr.generatelocationTransNo(siteId, stockPointId);

       MstProductVO productVO = BeanMapUtils.mapTo(productRepo.findByProductId(productId), MstProductVO.class);

       List<StockLocationTransactionVO> locTransVOList = new ArrayList<>();

       StockLocationTransactionVO locTransOutVO = buildStockLocationTransVO(siteId, 0, LocationMoveType.MOVEMENT_OUT, locTransNo
                                                                           , productId, stockPointId, fromLocationId, targetQty
                                                                           , picId, sysDate, sysTime, productVO);

       StockLocationTransactionVO locTransInVO = buildStockLocationTransVO(siteId, 1, LocationMoveType.MOVEMENT_IN, locTransNo
                                                                           , productId, stockPointId, toLocationId, targetQty
                                                                           , picId, sysDate, sysTime, productVO);

       locTransVOList.add(locTransOutVO);
       locTransVOList.add(locTransInVO);

       stockLocTransRepo.saveInBatch(BeanMapUtils.mapListTo(locTransVOList, StockLocationTransaction.class));
   }

    private void doStockAdjust(String siteId
                               , Long pointId
                               , String adjustType
                               , Long fromLocationId
                               , Long toLocationId
                               , Long productId
                               , String reasonId
                               , BigDecimal adjustedQty
                               , BigDecimal productCost
                               , String event
                               , Long personId
                               , String personNm) {

       String PARTS = ProductClsType.PART.getCodeDbid();

       ProductInventoryVO productInventoryVO = BeanMapUtils.mapTo(productInventoryRepo.findProductInventoryByLocationId(siteId, productId, pointId, fromLocationId), ProductInventoryVO.class);
       doUpdateProductInventoryQtyByCondition(siteId, pointId, productId, fromLocationId, adjustedQty, productInventoryVO, event);

       Set<Long> productIds = new HashSet<>();
       productIds.add(productId);

       MstProductVO productVO = BeanMapUtils.mapTo(productRepo.findByProductId(productId), MstProductVO.class);
       String productCd = productVO != null? productVO.getProductCd() : "";
       String productNm = productVO != null? productVO.getSalesDescription() : "";

       // 找到productStockstatusVOs
       List<ProductStockStatusVO> productStockStatusVOs = BeanMapUtils.mapListTo(productStockStsRepo.findStockStatusList(siteId, pointId, productIds, PARTS, paramStockStatusTypeIn()), ProductStockStatusVO.class);

       BigDecimal stockQty = productStockStatusVOs.stream().map(item -> item.getQuantity() != null ? item.getQuantity() : BigDecimal.ZERO)
               .reduce(BigDecimal.ZERO, BigDecimal::add);

       LocationVO locationVO = BeanMapUtils.mapTo(locationRepo.findByLocationId(toLocationId), LocationVO.class);


       Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();
       Map<String, Map<Long, ProductStockStatusVO>> dbStockMap = buildProductStockStatusVOMap(productStockStatusVOs, new HashMap<>());

       if (StringUtils.equals(adjustType, StockAdjustmentType.NORMALADJUSTMENT.getCodeDbid())) {
           if (StringUtils.equals(event, PLUS)) {
               generateStockStatusVOMap(siteId, pointId, productId, adjustedQty, SpStockStatus.ONHAND_QTY.getCodeDbid(), stockStatusVOChangeMap);
           } else if (StringUtils.equals(event, MINUS) ) {
               if (dbStockMap.containsKey(SpStockStatus.ONHAND_QTY.getCodeDbid())) {
                   Map<Long, ProductStockStatusVO> productStockStsMap = dbStockMap.get(SpStockStatus.ONHAND_QTY.getCodeDbid());
                   BigDecimal onHandQty = productStockStsMap.containsKey(productId)? productStockStsMap.get(productId).getQuantity() : BigDecimal.ZERO;
                   if (onHandQty.compareTo(adjustedQty) >= 0) {
                       generateStockStatusVOMap(siteId, pointId, productId, adjustedQty.multiply(BigDecimal.valueOf(-1)), SpStockStatus.ONHAND_QTY.getCodeDbid(), stockStatusVOChangeMap);
                   } else {
                       generateStockStatusVOMap(siteId, pointId, productId, onHandQty.multiply(BigDecimal.valueOf(-1)), SpStockStatus.ONHAND_QTY.getCodeDbid(), stockStatusVOChangeMap);
                   }
                   generateStockStatusVOMap(siteId, pointId, productId, onHandQty.subtract(adjustedQty), SpStockStatus.ALLOCATED_QTY.getCodeDbid(), stockStatusVOChangeMap);
               }
           }
       }

       // save productStockStatusVOs
       this.updateProductStockStatusByMap(stockStatusVOChangeMap);

       InventoryTransactionVO invTranVO = null;
       ProductCostVO avgProductCostVO;

       if (StringUtils.equals(event, PLUS)) {
           avgProductCostVO = costMgr.doCostCalculation(siteId, productId, productCost, adjustedQty, BigDecimal.ZERO, pointId);
         //获取productCostVOs(cost_type = S011AVERAGECOST)
           invTranVO = generateInventoryTransactionVO(siteId, InOutType.IN, pointId, null, pointId, productId, productCd, productNm
                                                   , InventoryTransactionType.ADJUSTIN.getCodeDbid(), adjustedQty, stockQty, productCost
                                                   , null, null, null, null, fromLocationId, avgProductCostVO, reasonId, personId, personNm, ProductClsType.PART.getCodeDbid());
       } else if (StringUtils.equals(event, MINUS)) {
           if (locationVO == null) {
               avgProductCostVO =BeanMapUtils.mapTo(productCostRepo.findByProductIdAndCostTypeAndSiteId(productId,CostType.AVERAGE_COST,siteId), ProductCostVO.class) ;
               invTranVO = generateInventoryTransactionVO(siteId, InOutType.OUT, pointId, pointId, null, productId, productCd, productNm
                                                   , InventoryTransactionType.ADJUSTOUT.getCodeDbid(), adjustedQty, stockQty, avgProductCostVO.getCost()
                                                   , null, null, null, null, fromLocationId, avgProductCostVO, reasonId, personId, personNm, ProductClsType.PART.getCodeDbid());
           }
       }

       if (invTranVO != null) {
            inventoryTransRepo.save(BeanMapUtils.mapTo(invTranVO, InventoryTransaction.class));
       }
   }

   private void updatePickedItemStockStatus(Long facilityId, Long partsId, String siteId, BigDecimal finalPickingQuantity) {

       //a. Allocated status qty descreases.
       doUpdateProductStockStatusMinusQty(siteId, facilityId, partsId, SpStockStatus.ALLOCATED_QTY.getCodeDbid(), finalPickingQuantity);

       //b. OnPicking status qty inscreases.
       doUpdateProductStockStatusPlusQty(siteId, facilityId, partsId, SpStockStatus.ONPICKING_QTY.getCodeDbid(), finalPickingQuantity);
   }

   private BigDecimal processNormalLocationTypeInventorys(List<ProductInventoryVO> normalLocationNoMainInvs
                                                       , Long partsId
                                                       , ProductInventoryVO mainLocationTypeInv
                                                       , BigDecimal remainQty
                                                       , List<PickingResultItemBO> resultItems
                                                       , Map<Long, LocationVO> locationMap) {

       // Process normal locations.
       if (mainLocationTypeInv != null) { // Find main location type.
           BigDecimal invQty = mainLocationTypeInv.getQuantity();
           BigDecimal pickQty = (invQty.compareTo(remainQty) >= 0) ? remainQty : invQty;
           this.addPickingResultItemAndUpdateInventoryQty(resultItems, partsId, mainLocationTypeInv, pickQty, locationMap);
           remainQty = remainQty.subtract(pickQty);
       }
       // Main location type is not enough, continue process other normal
       if (remainQty.signum() == 1 && normalLocationNoMainInvs.size() > 0) {
           remainQty = processOtherNormalTypeInventorys(normalLocationNoMainInvs, resultItems, partsId, remainQty, locationMap);
       }

       return remainQty;
   }

   private BigDecimal processOtherNormalTypeInventorys(List<ProductInventoryVO> normalLocationNoMainInvs
                                                       , List<PickingResultItemBO> resultItems
                                                       , Long partsId
                                                       , BigDecimal remainQty
                                                       , Map<Long, LocationVO> locationMap) {

       final BigDecimal fnlRemailQty = remainQty;
       final ProductInventoryVO[] equalQtyNormalInv = new ProductInventoryVO[1];
       //Sort by qty asc
       Collections.sort(normalLocationNoMainInvs
                       ,  new java.util.Comparator<ProductInventoryVO>() {
                           @Override
                        public int compare(ProductInventoryVO o1, ProductInventoryVO o2) {
                               BigDecimal qty1 = o1.getQuantity();
                               BigDecimal qty2 = o2.getQuantity();
                               if(qty1.compareTo(fnlRemailQty) ==0) {
                                   equalQtyNormalInv[0] = o1;
                               }else if(qty2.compareTo(fnlRemailQty) ==0){
                                   equalQtyNormalInv[0] = o2;
                               }
                               return o1.getQuantity().compareTo(o2.getQuantity());
                           }
                         }
       );

       return pickFromNormalNoMainInventorys(normalLocationNoMainInvs, resultItems, partsId, remainQty, equalQtyNormalInv[0], locationMap);
   }

   /**
    * Pick from normal location excluding main type.
    * @param normalLocationNoMainInvs
    * @param resultItems
    * @param partsId
    * @param remainQty
    * @param equalQtyNormalInv
    * @return
    */
    private BigDecimal pickFromNormalNoMainInventorys( List<ProductInventoryVO> normalLocationNoMainInvs
                                                       , List<PickingResultItemBO> resultItems
                                                       , Long partsId
                                                       , BigDecimal remainQty
                                                       , ProductInventoryVO equalQtyNormalInv
                                                       , Map<Long, LocationVO> locationMap) {

       if(remainQty.signum() <=0 || normalLocationNoMainInvs.isEmpty()) {
           return remainQty;
       }

       if(equalQtyNormalInv !=null) {
           this.addPickingResultItemAndUpdateInventoryQty(resultItems
                                                         , partsId
                                                         , equalQtyNormalInv
                                                         , remainQty
                                                         , locationMap);
           remainQty = BigDecimal.ZERO;

       } else {
           //Because list is sorted by asc, so get the first one.
           ProductInventoryVO inv  = normalLocationNoMainInvs.remove(0);//fetch the first one,
           BigDecimal invQty = inv.getQuantity();
           BigDecimal finalPickingQty = (invQty.compareTo(remainQty) >=0) ? remainQty : invQty;
           this.addPickingResultItemAndUpdateInventoryQty(resultItems
                                                         , partsId
                                                         , inv
                                                         , finalPickingQty
                                                         , locationMap);
           remainQty = remainQty.subtract(finalPickingQty); // remainQty-finalPickingQty

           //Loop the list ,find the qty-equal item.
           for (ProductInventoryVO normalLocationNoMainInv : normalLocationNoMainInvs) { //Here list is already by asc sort.
                 invQty = normalLocationNoMainInv.getQuantity();
                 int cmpInt = invQty.compareTo(remainQty);
                 if(cmpInt ==0) { // invQty==remainQty
                     equalQtyNormalInv = normalLocationNoMainInv;
                 } else if(cmpInt == 1) { // invQty > remainQty
                    break;
                 }
           }

           //Next call iteself
           return pickFromNormalNoMainInventorys(normalLocationNoMainInvs
                                                  , resultItems
                                                  , partsId
                                                  , remainQty
                                                  , equalQtyNormalInv
                                                  , locationMap);
       }
       return remainQty;
   }

   private void addPickingResultItemAndUpdateInventoryQty(List<PickingResultItemBO> resultItems
                                                       , Long partsId
                                                       , ProductInventoryVO proInv
                                                       , BigDecimal finalPickingQty
                                                       , Map<Long, LocationVO> locationMap) {

        PickingResultItemBO item = new PickingResultItemBO();

        item.setPartsId(partsId);
        item.setPickingLocation(locationMap.get(proInv.getLocationId()));
        item.setPickingQuantity(finalPickingQty);

        resultItems.add(item);

        //Update inventory quantity.
        doUpdateProductInventoryMinusQty(proInv, finalPickingQty, locationMap);
   }

   private void doUpdateProductInventoryMinusQty(ProductInventoryVO productInventoryInfo, BigDecimal qty, Map<Long, LocationVO> locationMap ){

       BigDecimal originalQty = productInventoryInfo.getQuantity();

       if  (originalQty.subtract(qty).compareTo(BigDecimal.ZERO) == -1) {

           MstFacility facility = facilityRepo.findByFacilityId(productInventoryInfo.getFacilityId());
           MstProduct product = productRepo.findByProductId(productInventoryInfo.getProductId());

           throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00316", new String[]{
                   CodedMessageUtils.getMessage("label.point")
                   , facility != null? facility.getFacilityCd() : StringUtils.EMPTY
                   , CodedMessageUtils.getMessage("label.product")
                   , product != null? product.getProductCd() : StringUtils.EMPTY
                   , CodedMessageUtils.getMessage("label.location")
                   , locationMap.containsKey(productInventoryInfo.getLocationId())? locationMap.get(productInventoryInfo.getLocationId()).getLocationCd() : ""
                   , CodedMessageUtils.getMessage("label.tableProductInventory") }));
       }

       productInventoryInfo.setQuantity(originalQty.subtract(qty));
       if (productInventoryInfo.getQuantity().compareTo(BigDecimal.ZERO) == 0 && !isMainLocation(productInventoryInfo)) {
           // delete the record when non-primary inventory's quantity will be zero
           productInventoryRepo.delete(BeanMapUtils.mapTo(productInventoryInfo, ProductInventory.class));
       } else {
           productInventoryRepo.save(BeanMapUtils.mapTo(productInventoryInfo, ProductInventory.class));
       }
   }

   private boolean isMainLocation(ProductInventoryVO proInv) {

       return StringUtils.equals(proInv.getPrimaryFlag(), CommonConstants.CHAR_Y);
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

//    private ProductStockStatusVO buildProductStockStatusVO(String siteId, Long facilityId, Long productId, BigDecimal targetQty, String targetStockStatus) {
//
//        ProductStockStatusVO productStockStsVO = new ProductStockStatusVO();
//
//        productStockStsVO.setSiteId(siteId);
//        productStockStsVO.setFacilityId(facilityId);
//        productStockStsVO.setProductId(productId);
//        productStockStsVO.setProductStockStatusType(targetStockStatus);
//        productStockStsVO.setQuantity(targetQty);
//        productStockStsVO.setProductClassification(PARTS);
//
//        return productStockStsVO;
//    }

    /**
     * 构建库存地点交易视图对象
     * 此方法用于根据给定的参数构建一个库存地点交易的视图对象(StockLocationTransactionVO)
     * 它将各个参数值映射到视图对象的相应属性中，以便于后续处理或存储
     *
     * @param siteId 网站ID，标识库存地点所属的网站
     * @param seq 序号，用于标识和排序
     * @param locTransType 交易类型，表示库存地点交易的性质（如入库、出库）
     * @param locTransNo 交易编号，用于唯一标识一次交易
     * @param productId 产品ID，标识交易涉及的产品
     * @param stockPointId 库存点ID，标识交易发生的库存点
     * @param locationId 地点ID，标识交易发生的地点
     * @param targetQty 目标数量，表示交易涉及的产品数量
     * @param picId 负责人ID，标识负责此次交易的人员
     * @param sysDate 交易日期，表示交易发生的日期
     * @param sysTime 交易时间，表示交易发生的具体时间
     * @param productVO 产品视图对象，包含产品的详细信息
     * @return StockLocationTransactionVO 返回构建的库存地点交易视图对象
     */
    private StockLocationTransactionVO buildStockLocationTransVO(String siteId, int seq, String locTransType, String locTransNo, Long productId
                                                                    , Long stockPointId, Long locationId
                                                                    , BigDecimal targetQty, Long picId
                                                                    , String sysDate, String sysTime
                                                                    , MstProductVO productVO) {

        StockLocationTransactionVO locTransVO = new StockLocationTransactionVO();

        locTransVO.setSiteId(siteId);
        locTransVO.setSeqNo(seq);
        locTransVO.setLocationTransactionType(locTransType);
        locTransVO.setTransactionNo(locTransNo);
        locTransVO.setProductId(productId);
        // 根据产品视图对象设置产品代码和名称，如果对象为空，则默认为空字符串
        locTransVO.setProductCd(productVO != null ? productVO.getProductCd() : "");
        locTransVO.setProductNm(productVO != null ? productVO.getSalesDescription() : "");
        locTransVO.setPersonId(picId);
        locTransVO.setFacilityId(stockPointId);
        locTransVO.setLocationId(locationId);
        locTransVO.setMoveQty(targetQty);
        locTransVO.setTransactionDate(sysDate);
        locTransVO.setTransactionTime(sysTime);

        return locTransVO;
    }

    private void insertProductInventoryVO(String siteId
                                        , Long facilityId
                                        , Long productId
                                        , Long locationId
                                        , String primaryFlag
                                        , BigDecimal qty
                                        , String productClassification) {

        ProductInventoryVO productInventoryVO = new ProductInventoryVO();

        productInventoryVO.setSiteId(siteId);
        productInventoryVO.setFacilityId(facilityId);
        productInventoryVO.setProductId(productId);
        productInventoryVO.setLocationId(locationId);
        productInventoryVO.setPrimaryFlag(primaryFlag);
        productInventoryVO.setQuantity(qty);
        productInventoryVO.setProductClassification(productClassification);

        productInventoryRepo.save(BeanMapUtils.mapTo(productInventoryVO, ProductInventory.class));
    }

    private void generateStockStatusVOChangeMap(Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap
                                              , String siteId
                                              , Long facilityId
                                              , Set<Long> productIds
                                              , Set<String> stockStatusType) {

        List<ProductStockStatusVO> stockStatusVOs = BeanMapUtils.mapListTo(productStockStsRepo.findStockStatusList(siteId, facilityId, productIds, ProductClsType.PART.getCodeDbid(), stockStatusType), ProductStockStatusVO.class);

        // 构建Map<String, Map<Long, ProductStockStatusVO>>
        buildProductStockStatusVOMap(stockStatusVOs, stockStatusVOChangeMap);
    }

    public Map<String, Map<Long, ProductStockStatusVO>> buildProductStockStatusVOMap(List<ProductStockStatusVO> stockStatusVOList, Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap) {

        for (ProductStockStatusVO item : stockStatusVOList) {
            Map<Long, ProductStockStatusVO> productStsMap;
            if (stockStatusVOChangeMap.containsKey(item.getProductStockStatusType())) {

                productStsMap = stockStatusVOChangeMap.get(item.getProductStockStatusType());
                if (!productStsMap.containsKey(item.getProductId())) {
                    productStsMap.put(item.getProductId(), item);

                    stockStatusVOChangeMap.put(item.getProductStockStatusType(), productStsMap);
                }
            } else {

                productStsMap = new HashMap<>();
                productStsMap.put(item.getProductId(), item);

                stockStatusVOChangeMap.put(item.getProductStockStatusType(), productStsMap);
            }
        }

        return stockStatusVOChangeMap;
    }

    public ProductStockStatus doGetProductStockStatusInfo(String siteId
                                                        , Long facilityId
                                                        , Long productId
                                                        , String productStockStatusTypeID) {
        return this.productStockStsRepo.findProductStockStatus(siteId, facilityId, productId, productStockStatusTypeID);

    }

    public List<ProductStockStatusVO> findStockStatusList(String siteId
                                                , Long facilityId
                                                , Set<Long> productIds
                                                , String productStockStatusTypeID) {
        Set<String> productStockStatusTypes = new HashSet<>();
        productStockStatusTypes.add(productStockStatusTypeID);
        List<ProductStockStatus> stockList = this.productStockStsRepo
                                                 .findStockStatusList(siteId
                                                                     , facilityId
                                                                     , productIds
                                                                     , ProductClsType.PART.getCodeDbid()
                                                                     , productStockStatusTypes);

        return BeanMapUtils.mapListTo(stockList, ProductStockStatusVO.class);

    }

    public void doPurchaseOrderRegister(String purchaseOrderType
                                       ,List<PurchaseOrderItemVO> purchaseOrderItems
                                       ,Long facilityId
                                       ,String siteId){

        if(purchaseOrderType.equals(PurchaseOrderPriorityType.POEO.getCodeDbid())){

            for (PurchaseOrderItemVO item : purchaseOrderItems){
                Long productId = item.getProductId();
                BigDecimal qty = item.getOnPurchaseQty();
                doUpdateProductStockStatusPlusQty(siteId, facilityId, productId, SpStockStatus.EO_ONPURCHASE_QTY.getCodeDbid(), qty);
            }

        }
        if(purchaseOrderType.equals(PurchaseOrderPriorityType.PORO.getCodeDbid())){

            for (PurchaseOrderItemVO item : purchaseOrderItems){
                Long productId = item.getProductId();
                BigDecimal qty = item.getOnPurchaseQty();
                doUpdateProductStockStatusPlusQty(siteId, facilityId, productId, SpStockStatus.RO_ONPURCHASE_QTY.getCodeDbid(), qty);
            }

        }
        if(purchaseOrderType.equals(PurchaseOrderPriorityType.POHO.getCodeDbid())){

            for (PurchaseOrderItemVO item : purchaseOrderItems){
                Long productId = item.getProductId();
                BigDecimal qty = item.getOnPurchaseQty();
                doUpdateProductStockStatusPlusQty(siteId, facilityId, productId, SpStockStatus.HO_ONPURCHASE_QTY.getCodeDbid(), qty);
            }


        }
        //Add by cxf for whole sale point
        if(purchaseOrderType.equals(PurchaseOrderPriorityType.POWO.getCodeDbid())){

            for (PurchaseOrderItemVO item : purchaseOrderItems){
                Long productId = item.getProductId();
                BigDecimal qty = item.getOnPurchaseQty();
                doUpdateProductStockStatusPlusQty(siteId, facilityId, productId, SpStockStatus.WO_ONPURCHASE_QTY.getCodeDbid(), qty);
            }

        }
    }

    /**
     *
     * @param stockStatusVOChangeMap
     */
    public void updateProductStockStatusByMap(Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap){

        String stockStatus;
        List<ProductStockStatusVO> listForSubtract;
        List<ProductStockStatusVO> listForAdd;
        ProductStockStatusVO productStockStatusVO;

        for (Entry<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeEntry : stockStatusVOChangeMap.entrySet()){

            stockStatus = stockStatusVOChangeEntry.getKey();

            listForSubtract = stockStatusVOChangeEntry.getValue().values().stream().filter(v -> v.getQuantity().signum() < 0).toList();
            listForAdd = stockStatusVOChangeEntry.getValue().values().stream().filter(v -> v.getQuantity().signum() > 0).toList();

            listForSubtract.forEach(t-> t.setQuantity(t.getQuantity().abs()));

            if (!listForSubtract.isEmpty()){

                productStockStatusVO = listForSubtract.get(0);

                //Parts逻辑专用，productStockStatusVOList中的必须为同一site，同一facility,且parts必须无重复(逻辑中已将数量累加)
                this.saveProductStockStatusForLargeSizeInSameSiteFacility(productStockStatusVO.getSiteId()
                                                                        , productStockStatusVO.getFacilityId()
                                                                        , stockStatus
                                                                        , CommonConstants.CHAR_BLANK
                                                                        , ProductClsType.PART.getCodeDbid()
                                                                        , listForSubtract);
            }

            if (!listForAdd.isEmpty()){

                productStockStatusVO = listForAdd.get(0);

                this.saveProductStockStatusForLargeSizeInSameSiteFacility(productStockStatusVO.getSiteId()
                                                                        , productStockStatusVO.getFacilityId()
                                                                        , CommonConstants.CHAR_BLANK
                                                                        , stockStatus
                                                                        , ProductClsType.PART.getCodeDbid()
                                                                        , listForAdd);
            }
        }
    }

    public static final Integer MAX_SIZE = 500;
    /**
     *
     * @param siteId
     * @param facilityId
     * @param productStockStatusTypeFrom
     * @param productStockStatusTypeTo
     * @param productClassificationId
     * @param productStockStatusVOListAll
     */
    public void saveProductStockStatusForLargeSizeInSameSiteFacility(String siteId
                                                                   , Long facilityId
                                                                   , String productStockStatusTypeFrom
                                                                   , String productStockStatusTypeTo
                                                                   , String productClassificationId
                                                                   , List<ProductStockStatusVO> productStockStatusVOListAll) {

        //每500条提交一次(防止SQL过长)
        List<List<ProductStockStatusVO>> productStockStatusGroup = Stream.iterate(0, f -> f + 1)
                                                                  .limit((long) Math.ceil((double) productStockStatusVOListAll.size() / MAX_SIZE))
                                                                  .map(a -> productStockStatusVOListAll.stream().skip(Long.valueOf(a) * MAX_SIZE).limit(MAX_SIZE).toList())
                                                                  .toList();

        for (List<ProductStockStatusVO> productStockStatusVOList : productStockStatusGroup){

            if(StringUtils.isNotBlankText(productStockStatusTypeFrom)){

                this.saveProductStockStatusForSubtractStock(siteId, facilityId, productClassificationId, productStockStatusTypeFrom, productStockStatusVOList);
            }

            if(StringUtils.isNotBlankText(productStockStatusTypeTo)){

                this.saveProductStockStatusForAddStock(siteId, facilityId, productClassificationId, productStockStatusTypeTo, productStockStatusVOList);
            }
        }
    }

    private void saveProductStockStatusForSubtractStock(String siteId, Long facilityId, String productClassificationId, String productStockStatusType, List<ProductStockStatusVO> productStockStatusVOList){

        Set<Long> relationProductIds = productStockStatusVOList.stream().map(ProductStockStatusVO::getProductId).collect(Collectors.toSet());

        Map<Long, Long> productIdStockIdMap = productStockStsRepo.getProductIdStockIdMap(siteId, facilityId, productClassificationId, productStockStatusType, relationProductIds);

        //检查DB中是否存在所有的库存数据, 不存在则直接报错
        if(!productIdStockIdMap.keySet().containsAll(relationProductIds)){
            throw new BusinessCodedException("Product stock is not exist,update failed");
        }

        Set<Long> productStockStatusIds = productIdStockIdMap.values().stream().collect(Collectors.toSet());

        //productStockStatusVOList和productStockStatusIds必须为1:1
        productStockStsRepo.updateStockStatusQuantitySubForStockList(productStockStatusVOList, productStockStatusIds);

        //再查询，如果存在Qty被扣减为负数的数据，则报错。
        if (productStockStsRepo.countStockStatusQuantitySubLessZero(productStockStatusIds) > 0){throw new BusinessCodedException("Product stock has been updated,update failed");}
    }

    /**
     * @param siteId
     * @param facilityId
     * @param productClassificationId
     * @param productStockStatusType
     * @param productStockStatusVOList
     */
    private void saveProductStockStatusForAddStock(String siteId, Long facilityId, String productClassificationId, String productStockStatusType, List<ProductStockStatusVO> productStockStatusVOList){

        Map<Long, Long> productStockStatusToMap = productStockStsRepo.getProductIdStockIdMap(siteId
                                                                                                , facilityId
                                                                                                , productClassificationId
                                                                                                , productStockStatusType
                                                                                                , productStockStatusVOList.stream().map(ProductStockStatusVO::getProductId).collect(Collectors.toSet()));

        Set<Long> productStockStatusUpdateIds = new HashSet<>();
        List<ProductStockStatusVO> productStockStatusUpdateList = new ArrayList<>();
        List<ProductStockStatusVO> productStockStatusAddList = new ArrayList<>();

        for(ProductStockStatusVO productStockStatus : productStockStatusVOList){

            if (!productStockStatusToMap.containsKey(productStockStatus.getProductId())){

                productStockStatus.setProductStockStatusId(IdUtils.getSnowflakeIdWorker().nextId());
                productStockStatus.setProductStockStatusType(productStockStatusType);
                productStockStatus.setProductClassification(productClassificationId);
                productStockStatus.setLastUpdatedBy(ThreadLocalPJAuditableDetailAccessor.getValue().getUpdateProgram());
                productStockStatusAddList.add(productStockStatus);

            } else {

                productStockStatusUpdateList.add(productStockStatus);
                productStockStatusUpdateIds.add(productStockStatusToMap.get(productStockStatus.getProductId()));
            }
        }

        if (!productStockStatusUpdateList.isEmpty()){productStockStsRepo.updateStockStatusQuantityAddForStockList(productStockStatusUpdateList, productStockStatusUpdateIds);}

        if (!productStockStatusAddList.isEmpty()){

            productStockStatusToMap = productStockStsRepo.getProductIdStockIdMap(siteId, facilityId, productClassificationId, productStockStatusType, productStockStatusAddList.stream().map(ProductStockStatusVO::getProductId).collect(Collectors.toSet()));

            if (!productStockStatusToMap.isEmpty()){
                throw new BusinessCodedException("Product stock has been updated,update failed");
            }

            productStockStsRepo.saveAll(BeanMapUtils.mapListTo(productStockStatusAddList, ProductStockStatus.class));
        }
    }

    /**
     * @param siteId
     * @param targetPointId
     * @param fromPointId
     * @param toPointId
     * @param productId
     * @param transactionTypeId
     * @param receiptQty
     * @param receiptPrice
     * @param relatedSlipId
     * @param relatedSlipNo
     * @param fromOrganizationId
     * @param toOrganizationId
     * @param locationId
     * @param reasonId
     * @return
     */
    public InventoryTransactionVO doRegisterInventoryTransaction(String siteId,
                                                                     Long targetPointId,
                                                                     Long fromPointId,
                                                                     Long toPointId,
                                                                     Long productId,
                                                                     String transactionTypeId,
                                                                     BigDecimal receiptQty,
                                                                     BigDecimal receiptPrice,
                                                                     Long relatedSlipId,
                                                                     String relatedSlipNo,
                                                                     Long fromOrganizationId,
                                                                     Long toOrganizationId,
                                                                     Long locationId,
                                                                     String reasonId
                                                                     ) {

        List<ConstantsBO> bos = constantsLogic.getConstantsData(PJConstants.InventoryTransactionType.class.getDeclaredFields());
        Map<String, String> transactionTypeMaps = bos.stream().filter(bo -> !Nulls.isNull(bo.getCodeDbid(), bo.getCodeData2())).collect(Collectors.toMap(ConstantsBO::getCodeDbid, ConstantsBO::getCodeData2));
        String inOutType = transactionTypeMaps.get(transactionTypeId);

        if (inOutType == null) {
            inOutType = CommonConstants.CHAR_BLANK;
        }

        ProductCostVO avgProductCostVO = BeanMapUtils.mapTo(productCostRepo.findByProductIdAndCostTypeAndSiteId(targetPointId, CostType.AVERAGE_COST, siteId), ProductCostVO.class);

        InventoryTransactionVO invTranVO = new InventoryTransactionVO();
        invTranVO.setSiteId(siteId);
        invTranVO.setInventoryTransactionType(transactionTypeId);
        invTranVO.setTargetFacilityId(targetPointId);
        invTranVO.setFromFacilityId(fromPointId);
        invTranVO.setToFacilityId(toPointId);
        invTranVO.setProductId(productId);
        invTranVO.setInQty(inOutType.equalsIgnoreCase(PJConstants.InOutType.IN) ? receiptQty.setScale(2,RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2));
        invTranVO.setOutQty(inOutType.equalsIgnoreCase(PJConstants.InOutType.OUT)? receiptQty.setScale(2,RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2));
        invTranVO.setCurrentQty(doCalculateCurrentStock(siteId, targetPointId, productId,inOutType,receiptQty).setScale(2));
        invTranVO.setInCost(inOutType.equalsIgnoreCase(PJConstants.InOutType.IN)? receiptPrice.setScale(CommonConstants.COST_FRAC_SCALE,RoundingMode.HALF_UP): BigDecimal.ZERO.setScale(CommonConstants.COST_FRAC_SCALE));
        invTranVO.setOutCost(inOutType.equalsIgnoreCase(PJConstants.InOutType.OUT)? receiptPrice.setScale(CommonConstants.COST_FRAC_SCALE,RoundingMode.HALF_UP): BigDecimal.ZERO.setScale(CommonConstants.COST_FRAC_SCALE));
        invTranVO.setCurrentAverageCost(!Nulls.isNull(avgProductCostVO) ? avgProductCostVO.getCost() : BigDecimal.ZERO);
        invTranVO.setPhysicalTransactionDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        invTranVO.setPhysicalTransactionTime(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_HMS));
        invTranVO.setRelatedSlipId(relatedSlipId);
        invTranVO.setRelatedSlipNo(relatedSlipNo);
        invTranVO.setFromOrganizationId(fromOrganizationId);
        invTranVO.setToOrganizationId(toOrganizationId);
        invTranVO.setStockAdjustmentReasonType(reasonId);
        invTranVO.setLocationId(locationId);

        return BeanMapUtils.mapTo(invTranVO, InventoryTransactionVO.class);
    }

    private BigDecimal doCalculateCurrentStock(String siteId, Long facilityId, Long productId, String inOutType, BigDecimal qty){

        BigDecimal result = BigDecimal.ZERO;

        if (inOutType.equalsIgnoreCase(CommonConstants.CHAR_BLANK)) {
            result = qty;
            return result;
        }

        List<ProductStockStatusVO> stockStatusVOList = BeanMapUtils.mapListTo(productStockStsRepo.findStockStatusListOneProduct(siteId
                                                                            , productId
                                                                            , ProductClsType.PART.getCodeDbid()
                                                                            , paramStockStatusTypeIn()), ProductStockStatusVO.class);

        Map<String, String> statusTypeMap = new HashMap<String, String>();
        statusTypeMap.put(SpStockStatus.ONHAND_QTY.getCodeDbid(), SpStockStatus.ONHAND_QTY.getCodeDbid());
        statusTypeMap.put(SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid(), SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid());
        statusTypeMap.put(SpStockStatus.ALLOCATED_QTY.getCodeDbid(), SpStockStatus.ALLOCATED_QTY.getCodeDbid());
        statusTypeMap.put(SpStockStatus.SHIPPING_REQUEST.getCodeDbid(), SpStockStatus.SHIPPING_REQUEST.getCodeDbid());
        statusTypeMap.put(SpStockStatus.ONSHIPPING.getCodeDbid(), SpStockStatus.ONSHIPPING.getCodeDbid());
        statusTypeMap.put(SpStockStatus.ONBORROWING_QTY.getCodeDbid(), SpStockStatus.ONBORROWING_QTY.getCodeDbid());
        statusTypeMap.put(SpStockStatus.ONSERVICE_QTY.getCodeDbid(), SpStockStatus.ONSERVICE_QTY.getCodeDbid());
        statusTypeMap.put(SpStockStatus.ONRECEIVING_QTY.getCodeDbid(), SpStockStatus.ONRECEIVING_QTY.getCodeDbid());
        statusTypeMap.put(SpStockStatus.ONFROZEN_QTY.getCodeDbid(), SpStockStatus.ONFROZEN_QTY.getCodeDbid());
        statusTypeMap.put(SpStockStatus.ONPICKING_QTY.getCodeDbid(), SpStockStatus.ONPICKING_QTY.getCodeDbid());
        for (ProductStockStatusVO productStockStatus : stockStatusVOList) {

            if (productStockStatus.getFacilityId() ==  facilityId
             && StringUtil.equals(productStockStatus.getSiteId(), siteId)) {

                String statusTypeId = productStockStatus.getProductStockStatusType();
                if (statusTypeMap.containsKey(statusTypeId))  {
                    result = result.add(productStockStatus.getQuantity());
                }
            }
        }

        return result;
    }

    public void doInventoryPickingLineCancel(String siteId, Long pointId, Long partsId, BigDecimal qty, Long locationId, ProductInventoryVO productInventoryVO) {

        doUpdateProductInventoryQtyByCondition(siteId, pointId, partsId, locationId, qty, productInventoryVO, CommonConstants.CHAR_PLUS);

        doUpdateProductStockStatusPlusQty(siteId, pointId, partsId, SpStockStatus.ALLOCATED_QTY.getCodeDbid(), qty);

        doUpdateProductStockStatusMinusQty(siteId, pointId, partsId, SpStockStatus.ONPICKING_QTY.getCodeDbid(), qty);
    }

    public void doPurchaseOrderCancellation(List<SpPurchaseCancelModelBO> spPurchaseCancelModelBOList) {

        for(SpPurchaseCancelModelBO item : spPurchaseCancelModelBOList) {
            if (PurchaseOrderPriorityType.POEO.getCodeDbid().equals(item.getEoRoType())) {
                this.doUpdateProductStockStatusMinusQty(item.getSiteId(), item.getConsigneeId(), item.getProductId(), SpStockStatus.EO_ONPURCHASE_QTY.getCodeDbid(), item.getMinusQuantity());
            } else if (PurchaseOrderPriorityType.PORO.getCodeDbid().equals(item.getEoRoType())) {
                this.doUpdateProductStockStatusMinusQty(item.getSiteId(), item.getConsigneeId(), item.getProductId(), SpStockStatus.RO_ONPURCHASE_QTY.getCodeDbid(), item.getMinusQuantity());
            } else if (PurchaseOrderPriorityType.POHO.getCodeDbid().equals(item.getEoRoType())) {
                this.doUpdateProductStockStatusMinusQty(item.getSiteId(), item.getConsigneeId(), item.getProductId(), SpStockStatus.HO_ONPURCHASE_QTY.getCodeDbid(), item.getMinusQuantity());
            } else if (PurchaseOrderPriorityType.POWO.getCodeDbid().equals(item.getEoRoType())) {
                this.doUpdateProductStockStatusMinusQty(item.getSiteId(), item.getConsigneeId(), item.getProductId(), SpStockStatus.WO_ONPURCHASE_QTY.getCodeDbid(), item.getMinusQuantity());
            }
        }
    }
    /**
     * 检查是否可以分配产品库存
     * <p>
     * 此方法用于判断请求的分配数量是否可以满足基于当前库存情况它首先检查传入的参数是否为空，
     * 如果有任何一个参数为空，则抛出PJCustomException异常然后，它尝试从数据库中查询指定站点、
     * 设施和产品的当前库存状态，并判断库存数量是否大于0如果库存数量大于0，则比较请求的分配数量
     * 和当前库存数量，如果请求的数量大于库存数量，则返回true，表示不能满足分配请求；否则返回false，
     * 表示可以满足分配请求如果找不到指定条件的库存状态，则默认返回false
     *
     * @param siteId 站点ID，不能为空
     * @param facilityId 仓库ID，不能为空
     * @param productId 产品ID，不能为空
     * @param allocateRequestQty 分配请求数量，不能为空
     * @return 如果请求的分配数量可以满足，则返回false；否则返回true如果找不到库存状态或库存数量为0，则也返回true
     * @throws PJCustomException 如果siteId、facilityId、productId和allocateRequestQty中任何一个为空，则抛出此异常
     */
    public boolean canDoAllocate(String siteId, Long facilityId, Long productId, BigDecimal allocateRequestQty){

        if (siteId == null || facilityId == null || productId == null || allocateRequestQty == null) {
            throw new PJCustomException("siteId, facilityId, productId and allocateRequestQty cannot be null");
        }

        ProductStockStatus productStockStatus = productStockStsRepo
                .findBySiteIdAndFacilityIdAndProductIdAndProductClassificationAndProductStockStatusType(siteId, facilityId, productId,
                        PJConstants.ProductClsType.GOODS.getCodeDbid(),
                        PJConstants.SdStockStatus.ONHAND_QTY.getCodeDbid());

        if (productStockStatus != null && productStockStatus.getQuantity() != null) {
            return productStockStatus.getQuantity().compareTo(allocateRequestQty) >= 0;
        } else {
            return false;
        }
    }

    /**
     * 处理单元零售出货完成
     * 该方法负责验证请求数量、查找配送订单、更新库存状态，并保存库存交易记录
     *
     * @param deliveryOrderItemVO 配送订单项视图对象，包含配送订单相关详细信息
     * @param requestQty 请求的数量，用于验证和更新库存状态
     */
    public void doUnitRetailShippingCompletion(DeliveryOrderVO deliveryOrderVO, DeliveryOrderItemVO deliveryOrderItemVO, BigDecimal requestQty){

        try {
            // 验证请求数量是否为负数
            this.validateNegativeRequestQuantity(requestQty);

            // 查询相关库存状态列表
            List<ProductStockStatus> productStockStatusList = this.queryProductStockStatus(deliveryOrderItemVO, deliveryOrderVO);

            // 处理分配库存状态
            ProductStockStatus allocatedProductStockStatus = this.processAllocatedStockStatus(deliveryOrderItemVO, productStockStatusList, requestQty, deliveryOrderVO.getFromFacilityId());

            // 处理已发货库存状态
            ProductStockStatus shippedProductStockStatus  = this.processShippedStockStatus(deliveryOrderItemVO, productStockStatusList, requestQty, deliveryOrderVO.getFromFacilityId());

            // 保存库存状态
            List <ProductStockStatus> saveList = new ArrayList<>();
            saveList.add(shippedProductStockStatus);
            saveList.add(allocatedProductStockStatus);
            productStockStsRepo.saveInBatch(saveList);

            // 生成并保存库存交易记录
            this.generateAndSaveInventoryTransaction(deliveryOrderItemVO, productStockStatusList, deliveryOrderVO);
        } catch (Exception e) {
            log.error("处理出货完成时发生异常", e);
            throw new PJCustomException("An exception occurred while processing the shipment was completed");
        }
    }

    private void validateNegativeRequestQuantity(BigDecimal requestQty) {
        if (requestQty.compareTo(BigDecimal.ZERO) < 0) {
            log.error("扣减数量不可为负数");
            throw new PJCustomException("The number of requests cannot be negative");
        }
    }

    private List<ProductStockStatus> queryProductStockStatus(DeliveryOrderItemVO deliveryOrderItemVO, DeliveryOrderVO deliveryOrderVO) {

        List<String> stockStatusTypes = Arrays.asList(
                PJConstants.SdStockStatus.ONHAND_QTY.getCodeDbid(),
                PJConstants.SdStockStatus.ONTRANSFER_IN_QTY.getCodeDbid(),
                PJConstants.SdStockStatus.ALLOCATED_QTY.getCodeDbid(),
                PJConstants.SdStockStatus.SHIPPING_REQUEST.getCodeDbid(),
                PJConstants.SdStockStatus.ONSHIPPING.getCodeDbid(),
                PJConstants.SdStockStatus.SHIPPED.getCodeDbid()
        );

        return productStockStsRepo.findBySiteIdAndFacilityIdAndProductIdAndProductClassificationAndProductStockStatusTypeIn(
                deliveryOrderItemVO.getSiteId(),
                deliveryOrderVO.getFromFacilityId(),
                deliveryOrderItemVO.getProductId(),
                PJConstants.ProductClsType.GOODS.getCodeDbid(),
                stockStatusTypes
        );
    }

    /**
     * 处理已分配库存状态
     * <p>
     * 本方法旨在处理配送订单项中的已分配库存状态它首先尝试查找代表已分配数量的库存状态，
     * 如果找到并且已分配的数量小于请求的数量，则抛出异常如果已分配的数量大于等于请求的数量，
     * 则从已分配的数量中减去请求的数量如果没有找到代表已分配数量的库存状态，则抛出自定义异常
     *
     * @param deliveryOrderItemVO 配送订单项的视图对象，包含订单项信息
     * @param productStockStatusList 产品库存状态列表，包含各种库存状态信息
     * @param requestQty 请求的数量，用于比较和更新已分配库存
     * @param fromFacilityId 设施ID，用于获取设施信息
     * @return 返回处理后的已分配库存状态对象如果处理失败，可能返回null
     * @throws BusinessCodedException 当已分配的数量小于请求的数量时抛出
     * @throws PJCustomException 当找不到已分配库存状态时抛出
     */
    private ProductStockStatus processAllocatedStockStatus(DeliveryOrderItemVO deliveryOrderItemVO, List<ProductStockStatus> productStockStatusList, BigDecimal requestQty, Long fromFacilityId) {

        ProductStockStatus allocatedProductStockStatus = productStockStatusList.stream()
                .filter(member -> StringUtils.equals(member.getProductStockStatusType(), PJConstants.SdStockStatus.ALLOCATED_QTY.getCodeDbid()))
                .findFirst()
                .orElse(null);

        if (allocatedProductStockStatus != null) {
            if (allocatedProductStockStatus.getQuantity().compareTo(requestQty) < 0){

                MstFacility facility = facilityRepo.findByFacilityId(fromFacilityId);
                Map<String, ConstantsBO> spStockStatusMap = constantsLogic.getConstantsMap(SpStockStatus.class.getDeclaredFields());
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00316", new String[]{
                        CodedMessageUtils.getMessage("label.point")
                        , Optional.ofNullable(facility).map(MstFacility::getFacilityCd).orElse(StringUtils.EMPTY)
                        , CodedMessageUtils.getMessage("label.product")
                        , deliveryOrderItemVO.getProductCd()
                        , CodedMessageUtils.getMessage("label.productStockStatus")
                        , spStockStatusMap.containsKey(PJConstants.SdStockStatus.ALLOCATED_QTY.getCodeDbid())? spStockStatusMap.get(PJConstants.SdStockStatus.ALLOCATED_QTY.getCodeDbid()).getCodeData1() : StringUtils.EMPTY
                        , CodedMessageUtils.getMessage("label.tableProductStockStatus") }));
            } else {
                allocatedProductStockStatus.setQuantity(allocatedProductStockStatus.getQuantity().subtract(requestQty));
            }
        } else {
            log.error("allocatedProductStockStatus操作失败");
            throw new PJCustomException(BaseResult.ERROR_MESSAGE);
        }
        return allocatedProductStockStatus;
    }

    /**
     * 处理发货库存状态
     *
     * @param deliveryOrderItemVO 配送订单项视图对象，包含配送订单的详细信息
     * @param productStockStatusList 产品库存状态列表，用于存储不同状态的库存信息
     * @param requestQty 请求的数量，用于更新发货库存状态的数量信息
     * @param fromFacilityId 发货设施的ID，用于设置发货库存状态的设施信息
     * @return 返回处理后的发货库存状态对象
     */
    private ProductStockStatus processShippedStockStatus(DeliveryOrderItemVO deliveryOrderItemVO, List<ProductStockStatus> productStockStatusList, BigDecimal requestQty, Long fromFacilityId) {

        ProductStockStatus shippedProductStockStatus = productStockStatusList.stream()
                .filter(s -> s.getProductStockStatusType().equals(PJConstants.SdStockStatus.SHIPPED.getCodeDbid()))
                .findFirst()
                .orElse(null);

        if (shippedProductStockStatus == null){
            shippedProductStockStatus = new ProductStockStatus();
            shippedProductStockStatus.setSiteId(deliveryOrderItemVO.getSiteId());
            shippedProductStockStatus.setFacilityId(fromFacilityId);
            shippedProductStockStatus.setProductId(deliveryOrderItemVO.getProductId());
            shippedProductStockStatus.setProductClassification(PJConstants.ProductClsType.GOODS.getCodeDbid());
            shippedProductStockStatus.setProductStockStatusType(SdStockStatus.SHIPPED.getCodeDbid());
        } else {
            shippedProductStockStatus.setQuantity(shippedProductStockStatus.getQuantity().add(requestQty));
        }
        return shippedProductStockStatus;
    }

    /**
     * 生成并保存库存交易信息
     * <p>
     * 此方法在处理送货单据时用于生成和保存库存交易信息它计算实际库存数量，
     * 根据送货单据和产品库存状态，然后生成库存交易信息并保存
     *
     * @param deliveryOrderItemVO 送货单据项视图对象，包含送货细节
     * @param productStockStatusList 产品库存状态列表，用于计算实际库存数量
     * @param deliveryOrder 送货单据对象，包含送货单据的总体信息
     */
    private void generateAndSaveInventoryTransaction(DeliveryOrderItemVO deliveryOrderItemVO, List<ProductStockStatus> productStockStatusList, DeliveryOrderVO deliveryOrderVO) {

        BigDecimal realStockQty = productStockStatusList.stream()
                .filter(member -> !StringUtils.equals(member.getProductStockStatusType(), PJConstants.SdStockStatus.SHIPPED.getCodeDbid()))
                .map(ProductStockStatus::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ProductCost productCost = productCostRepo.findBySiteIdAndProductIdAndCostType(deliveryOrderItemVO.getSiteId(), deliveryOrderItemVO.getProductId(), PJConstants.CostType.RECEIVE_COST);
        ProductCostVO productCostVO = BeanMapUtils.mapTo(productCost, ProductCostVO.class);

        this.generateInventoryTransactionVO(deliveryOrderItemVO.getSiteId(),
                OUT,
                deliveryOrderVO.getFromFacilityId(),
                deliveryOrderVO.getFromFacilityId(),
                null,
                deliveryOrderItemVO.getProductId(),
                deliveryOrderItemVO.getProductCd(),
                deliveryOrderItemVO.getProductNm(),
                deliveryOrderVO.getInventoryTransactionType(),
                deliveryOrderItemVO.getDeliveryQty(),
                realStockQty,
                deliveryOrderItemVO.getProductCost(),
                deliveryOrderVO.getDeliveryOrderId(),
                deliveryOrderVO.getDeliveryOrderNo(),
                deliveryOrderVO.getFromOrganizationId(),
                deliveryOrderVO.getToOrganizationId(),
                null,
                productCostVO,
                CommonConstants.CHAR_BLANK,
                null,
                CommonConstants.CHAR_BLANK,
                ProductClsType.GOODS.getCodeDbid());
    }

        /**
     *
     * @param siteId
     * @param facilityId
     * @param productId
     * @param targetQty
     * @param targetStockStatus
     * @param stockStatusVOChangeMap
     */
    public void generateStockStatusVOMapForSD(String siteId
                                        , Long facilityId
                                        , Long productId
                                        , BigDecimal targetQty
                                        , String targetStockStatus
                                        , Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap) {

        Map<Long, ProductStockStatusVO> valueMap = stockStatusVOChangeMap.containsKey(targetStockStatus) ? stockStatusVOChangeMap.get(targetStockStatus) : new HashMap<>();

        ProductStockStatusVO stockStatusVO = valueMap.get(productId);

        if (stockStatusVO == null){

            stockStatusVO = new ProductStockStatusVO();

            stockStatusVO.setSiteId(siteId);
            stockStatusVO.setProductId(productId);
            stockStatusVO.setFacilityId(facilityId);
            stockStatusVO.setQuantity(targetQty);
            stockStatusVO.setProductStockStatusType(targetStockStatus);
            stockStatusVO.setProductClassification(ProductClsType.GOODS.getCodeDbid());
            stockStatusVO.setUpdateProgram(ThreadLocalPJAuditableDetailAccessor.getValue().getUpdateProgram());
            stockStatusVO.setLastUpdatedBy(ThreadLocalPJAuditableDetailAccessor.getValue().getUpdateProgram());
        } else {

            stockStatusVO.setLastUpdatedBy(ThreadLocalPJAuditableDetailAccessor.getValue().getUpdateProgram());
            stockStatusVO.setQuantity(stockStatusVO.getQuantity().add(targetQty));
            stockStatusVO.setLastUpdatedBy(ThreadLocalPJAuditableDetailAccessor.getValue().getUpdateProgram());
        }

        valueMap.put(productId, stockStatusVO);
        stockStatusVOChangeMap.put(targetStockStatus, valueMap);
    }

        /**
     *
     * @param stockStatusVOChangeMap
     */
    public void updateProductStockStatusByMapForSD(Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap){

        String stockStatus;
        List<ProductStockStatusVO> listForSubtract;
        List<ProductStockStatusVO> listForAdd;
        ProductStockStatusVO productStockStatusVO;

        for (Entry<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeEntry : stockStatusVOChangeMap.entrySet()){

            stockStatus = stockStatusVOChangeEntry.getKey();

            listForSubtract = stockStatusVOChangeEntry.getValue().values().stream().filter(v -> v.getQuantity().signum() < 0).toList();
            listForAdd = stockStatusVOChangeEntry.getValue().values().stream().filter(v -> v.getQuantity().signum() > 0).toList();

            listForSubtract.forEach(t-> t.setQuantity(t.getQuantity().abs()));

            if (!listForSubtract.isEmpty()){

                productStockStatusVO = listForSubtract.get(0);

                this.saveProductStockStatusForLargeSizeInSameSiteFacility(productStockStatusVO.getSiteId()
                                                                        , productStockStatusVO.getFacilityId()
                                                                        , stockStatus
                                                                        , CommonConstants.CHAR_BLANK
                                                                        , ProductClsType.GOODS.getCodeDbid()
                                                                        , listForSubtract);
            }

            if (!listForAdd.isEmpty()){

                productStockStatusVO = listForAdd.get(0);

                this.saveProductStockStatusForLargeSizeInSameSiteFacility(productStockStatusVO.getSiteId()
                                                                        , productStockStatusVO.getFacilityId()
                                                                        , CommonConstants.CHAR_BLANK
                                                                        , stockStatus
                                                                        , ProductClsType.GOODS.getCodeDbid()
                                                                        , listForAdd);
            }
        }
    }
}