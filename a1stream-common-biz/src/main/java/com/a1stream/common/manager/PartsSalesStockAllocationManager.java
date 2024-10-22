package com.a1stream.common.manager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.bo.TargetOrderItemModel;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.OrderCancelReasonTypeSub;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ProductRelationClass;
import com.a1stream.common.constants.PJConstants.SalesOrderActionType;
import com.a1stream.common.constants.PJConstants.SpSalesStatusType;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.model.PartsInfoBO;
import com.a1stream.domain.bo.parts.TargetSalesOrderItemBO;
import com.a1stream.domain.entity.MstProduct;
import com.a1stream.domain.entity.MstProductRelation;
import com.a1stream.domain.entity.ProductCost;
import com.a1stream.domain.entity.ProductStockStatus;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.entity.SalesOrderItem;
import com.a1stream.domain.repository.MstProductRelationRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductCostRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;

@Component
public class PartsSalesStockAllocationManager {

    @Resource
    private InventoryManager inventoryManager;
    @Resource
    private SalesOrderManager salesOrderManager;
    @Resource
    private ProductManager productManager;
    @Resource
    private CostManager costManager;
    @Resource
    private SalesOrderRepository salesOrderRepository;
    @Resource
    private SalesOrderItemRepository salesOrderItemRepository;
    @Resource
    private MstProductRepository mstProductRepository;
    @Resource
    private ProductStockStatusRepository productStockStatusRepository;
    @Resource
    private ProductCostRepository productCostRepository;
    @Resource
    private MstProductRelationRepository mstProductRelationRepository;

    private static final String KEY_BOFLAG = "BOFLAG";
    private static final String KEY_REASON = "REASON";
    private static final String KEY_BOFLAG_BO = "1";
    private static final String KEY_BOFLAG_BO_CANCEL = "-1";
    public static final String COST_BO_OR_CANCEL_SIGN = "1";
    public static final String PRICE_BO_OR_CANCEL_SIGN = "-1";
    public static final String CHAR_NEGATIVE_ONE      = "-1";
    public static final int PRICE_FRAC_SCALE = 2;

    public void executeCancelAllocated(String siteId, Long deliveryPointId, Long productId, BigDecimal cancelledQty ) {

        List<SalesOrderVO> orderStatusInfos = BeanMapUtils.mapListTo(this.salesOrderRepository.findByOrderStatusAndSiteIdAndFacilityId(
                                                                     MstCodeConstants.SalesOrderStatus.SP_WAITINGPICKING
                                                                     ,siteId,deliveryPointId), SalesOrderVO.class);

        List<TargetOrderItemModel> allocatedCancelTarget = new ArrayList<>();
        TargetOrderItemModel releaseItem;

        Map<Long, SalesOrderVO> targetOrderIds = orderStatusInfos.stream()
                .collect(Collectors.toMap(SalesOrderVO::getSalesOrderId, obj->obj));


        List<SalesOrderItemVO> salesOrderItemVOs = BeanMapUtils.mapListTo(this.salesOrderItemRepository.findBySalesOrderIds(
                                                                          siteId,targetOrderIds.keySet()), SalesOrderItemVO.class);

        Map<Long, SalesOrderItemVO> itemMap = salesOrderItemVOs.stream()
                .collect(Collectors.toMap(SalesOrderItemVO::getSalesOrderItemId, obj->obj));

        BigDecimal canCancelQty;
        String allocateDueDate = CommonConstants.CHAR_BLANK;
        for (SalesOrderItemVO salesOrderItemVO : salesOrderItemVOs) {

            canCancelQty = salesOrderItemVO.getAllocatedQty() != null? salesOrderItemVO.getAllocatedQty(): BigDecimal.ZERO;

            if(salesOrderItemVO.getProductId().equals(productId) && canCancelQty.compareTo(BigDecimal.ZERO)== CommonConstants.INTEGER_ONE) {

                releaseItem = new TargetOrderItemModel();

                //系统当前时间
                LocalDate currentDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                String sysDate = currentDate.format(formatter);

                allocateDueDate = CommonConstants.CHAR_BLANK;
                allocateDueDate = targetOrderIds.get(salesOrderItemVO.getSalesOrderId()).getAllocateDueDate();
                allocateDueDate = CommonConstants.CHAR_BLANK.equals(allocateDueDate)? sysDate: allocateDueDate;

                releaseItem.setAllocateDueDate(allocateDueDate);
                releaseItem.setChangedQuantity(canCancelQty);
                releaseItem.setDeliveryPointId(deliveryPointId);
                releaseItem.setItemSeqNo(salesOrderItemVO.getSeqNo());
                releaseItem.setOrderId(salesOrderItemVO.getSalesOrderId());
                releaseItem.setOrderItemId(salesOrderItemVO.getSalesOrderItemId());
                releaseItem.setItemSeqNo(salesOrderItemVO.getSeqNo());
                releaseItem.setProductId(salesOrderItemVO.getProductId());
                releaseItem.setSiteId(siteId);
                allocatedCancelTarget.add(releaseItem);

            }
        }

        if(!allocatedCancelTarget.isEmpty()){

            //排序
            Collections.sort(allocatedCancelTarget, new TargetOrderItemModel.AllcoatedCancelPriorityComparator());
            BigDecimal totalRequestQty = cancelledQty;
            BigDecimal itemRequestQty;
            Set<Long> targetOrders = new HashSet<>();
            SalesOrderItemVO targetItem;
            SalesOrderItemVO supsendingOrderItem;

            List<SalesOrderItemVO> updateItemList = new ArrayList();
            for (TargetOrderItemModel member : allocatedCancelTarget) {

                targetItem = itemMap.get(member.getOrderItemId());
                itemRequestQty = targetItem.getAllocatedQty() != null? targetItem.getAllocatedQty(): BigDecimal.ZERO;

                if (itemRequestQty.compareTo(totalRequestQty) == 1){
                    itemRequestQty = totalRequestQty;
                    totalRequestQty = new BigDecimal(0);
                }else{
                    totalRequestQty = totalRequestQty.add(itemRequestQty.negate());
                }

                supsendingOrderItem = salesOrderManager.getLastSupersedingOrderItem(targetItem);

                // 如果 targetItem.getAllocatedQty() - itemRequestQty < 0
                if (targetItem.getAllocatedQty().subtract(itemRequestQty).compareTo(BigDecimal.ZERO) < 0) {
                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00314", new String[] {CodedMessageUtils.getMessage("label.orderNo"),member.getOrderNo(),CodedMessageUtils.getMessage("label.salesOrder")}));
                }
                targetItem.setAllocatedQty(targetItem.getAllocatedQty().subtract(itemRequestQty));
                updateItemList.add(targetItem);

                supsendingOrderItem.setBoQty(supsendingOrderItem.getBoQty().add(itemRequestQty));
                updateItemList.add(supsendingOrderItem);

                Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();
                inventoryManager.generateStockStatusVOMap(siteId, deliveryPointId, member.getProductId(), itemRequestQty, PJConstants.SpStockStatus.BO_QTY.getCodeDbid(), stockStatusVOChangeMap);
                inventoryManager.updateProductStockStatusByMap(stockStatusVOChangeMap);
                targetOrders.add(member.getOrderId());
            }

            //修改订单状态
            List<SalesOrderVO> orderStatus = BeanMapUtils.mapListTo(this.salesOrderRepository.findBySalesOrderIdIn(targetOrders),SalesOrderVO.class);
            orderStatus.forEach(obj -> obj.setOrderStatus(MstCodeConstants.SalesOrderStatus.WAITING_ALLOCATE));
            salesOrderRepository.saveInBatch(BeanMapUtils.mapListTo(orderStatus,SalesOrder.class));

            salesOrderItemRepository.saveInBatch(BeanMapUtils.mapListTo(updateItemList,SalesOrderItem.class));
        }
    }




    public SalesOrderVO executeStockAllocation(SalesOrderVO salesOrderInfo,List<SalesOrderItemVO> orderItemVoList){

        SalesOrderVO result = salesOrderInfo;
        List<SalesOrderItemVO> salesOrderitemList =orderItemVoList;
        if(salesOrderitemList.isEmpty()) return result;

       Map<String, SalesOrderItemVO> salesOrderItemsMapByProductKey = generateSalesOrderItemDataList(salesOrderitemList);

       Set<Long> relationProductInfos = initialRelationProductList(salesOrderItemsMapByProductKey);

       Map<Long, Long> oldWithNewSupersedingProductInfoMap = initialSupersedingProductMap(CommonConstants.CHAR_DEFAULT_SITE_ID, relationProductInfos);

       updateRelationProductList(relationProductInfos, oldWithNewSupersedingProductInfoMap);

       //获取含有waitingAllocate和Bo的明细行
       List<SalesOrderItemVO> targetOrderItemList =  initialTargetStockAllocationOrderItemList(salesOrderItemsMapByProductKey);
       //获取商品成本
       Map<Long, BigDecimal> productCostMap = initialProductCostMap(relationProductInfos,salesOrderInfo.getSiteId());

       if (targetOrderItemList.isEmpty()) return result;

       //获取部品的allocate、bo 和onhand库存数量
       Map<String, BigDecimal> productStockMap = initialProductStockMap(salesOrderInfo.getFacilityId()
                                                                      , relationProductInfos
                                                                      , salesOrderInfo.getSiteId());

       Map<String, BigDecimal> productStockChangedResult = new HashMap<>();

       List<SalesOrderItemVO> allocateResultModelList = allocateStockForOrderItems(result.getSiteId()
                                                                                   , result.getFacilityId()
                                                                                , oldWithNewSupersedingProductInfoMap
                                                                                , productStockMap
                                                                                , productStockChangedResult
                                                                                , salesOrderItemsMapByProductKey
                                                                                , targetOrderItemList
                                                                                , productCostMap
                                                                                , salesOrderInfo.getAllocateDueDate());

        //Update ProductStockStatusInfo
        updateProductStockStatusInfo(result.getSiteId(), result.getFacilityId(), productStockChangedResult);

        //Add for #4820, if have BO add one more line into orderFeature for boRelease
        BigDecimal boqtySum = BigDecimal.ZERO ;
        for (SalesOrderItemVO member : salesOrderitemList){

            BigDecimal boqty = member.getBoQty();
            boqtySum = boqtySum.add(boqty);

        }
        if (boqtySum != null && boqtySum.compareTo(CommonConstants.BIGDECIMAL_ZERO) >0 ) {

            result.setBoFlag(CommonConstants.CHAR_Y);
        }

        this.salesOrderItemRepository.saveInBatchWithVersionCheck(BeanMapUtils.mapListTo(allocateResultModelList, SalesOrderItem.class));
        result = salesOrderManager.doSetOrderStatus(result, SalesOrderActionType.STOCK_ALLOCATE);

        List<SalesOrderItem> tempList = this.salesOrderItemRepository.findBySalesOrderId(result.getSalesOrderId());
        BigDecimal amt = tempList.stream().filter(item -> item.getActualQty().compareTo(BigDecimal.ZERO) > 0)
                                          .map(item -> item.getActualAmt() != null ? item.getActualAmt() : BigDecimal.ZERO)
                                          .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal qty = tempList.stream().filter(item -> item.getActualQty().compareTo(BigDecimal.ZERO) > 0)
                                          .map(item -> item.getActualQty() != null ? item.getActualQty() : BigDecimal.ZERO)
                                          .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal amtNotVat = tempList.stream().filter(item -> item.getActualQty().compareTo(BigDecimal.ZERO) > 0)
                                                .map(item -> item.getActualAmtNotVat() != null ? item.getActualAmtNotVat() : BigDecimal.ZERO)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.setTotalActualAmt(amt);
        result.setTotalActualAmtNotVat(amtNotVat);
        result.setTotalActualQty(qty);

        this.salesOrderRepository.saveWithVersionCheck(BeanMapUtils.mapTo(result, SalesOrder.class));
        return result;

    }

//    private Map<Long, SalesOrderItemVO> prepareSalesOrderItemMapByOrderItemIdKey(Map<String, SalesOrderItemVO> salesOrderItemMapByProductKey){
//
//        Map<Long, SalesOrderItemVO> result = new HashMap<>();
//
//        Collection<SalesOrderItemVO> orderItemList = salesOrderItemMapByProductKey.values();
//        for (SalesOrderItemVO member:orderItemList){
//            result.put(member.getSalesOrderItemId(), member);
//        }
//
//        return result;
//    }

    private Map<Long, BigDecimal> initialProductCostMap(Set<Long> relationProductList,String siteId){

        Map<Long, BigDecimal> result = null;
        if (!relationProductList.isEmpty()){
            result = costManager.getProductCostInBulk(siteId, CostType.AVERAGE_COST, relationProductList);
        }else {
            result = new HashMap<>();
        }
        return result;
    }

    private boolean isSetProductAverageCost(String siteId, Long targetProductId, Map<Long, BigDecimal> productCostMap){

        boolean result = false;


        BigDecimal prodCost = null;

        // get target product cost
        if (productCostMap.containsKey(targetProductId)){
            prodCost = productCostMap.get(targetProductId);
        } else {
            ProductCost pCost = productCostRepository.findByProductIdAndCostTypeAndSiteId(targetProductId, CostType.AVERAGE_COST,siteId);
            if(pCost!=null) {

                prodCost = productCostRepository.findByProductIdAndCostTypeAndSiteId(targetProductId, CostType.AVERAGE_COST,siteId).getCost();
                productCostMap.put(targetProductId, prodCost);
            }
        }

        if (prodCost == null || prodCost.compareTo(BigDecimal.ZERO) == 0){
            result = false;
        } else {
            result = true;
        }

        return result;
    }

    private void updateProductStockStatusInfo(String siteId, Long targetPointId, Map<String, BigDecimal> productStockChangedResult){

        Long productId = null;
        String stockStatusTypeId = null;
        for(String prodStockKey : productStockChangedResult.keySet()){
            productId =Long.valueOf(prodStockKey.split("__")[0]);
            stockStatusTypeId = prodStockKey.split("__")[1];

            if(productStockChangedResult.get(prodStockKey).compareTo(BigDecimal.ZERO) > 0){
                inventoryManager.doUpdateProductStockStatusPlusQty(siteId, targetPointId, productId, stockStatusTypeId, productStockChangedResult.get(prodStockKey));
            }

            if(productStockChangedResult.get(prodStockKey).compareTo(BigDecimal.ZERO) < 0){
                inventoryManager.doUpdateProductStockStatusMinusQty(siteId, targetPointId, productId, stockStatusTypeId, productStockChangedResult.get(prodStockKey).abs());
            }
        }
    }

    private Map<String, BigDecimal> initialProductStockMap(Long targetPointId
                                                         , Set<Long> relationProductInfos
                                                         , String siteId){

        Map<String, BigDecimal> result = new HashMap<>();

        List<String> targetStockStatusTypes = new ArrayList<>();

        targetStockStatusTypes.add(SpStockStatus.ALLOCATED_QTY.getCodeDbid());
        targetStockStatusTypes.add(SpStockStatus.BO_QTY.getCodeDbid());
        targetStockStatusTypes.add(SpStockStatus.ONHAND_QTY.getCodeDbid());


        List<ProductStockStatus> prodStockStatusInfos = this.productStockStatusRepository
                                                            .findBySiteIdAndFacilityIdAndProductStockStatusTypeInAndProductIdIn(siteId, targetPointId, targetStockStatusTypes,new ArrayList<>(relationProductInfos) );

        for (Long proId : relationProductInfos){
            result.put(proId + "_" + SpStockStatus.ALLOCATED_QTY.getCodeDbid(), BigDecimal.ZERO);
            result.put(proId + "_" + SpStockStatus.BO_QTY.getCodeDbid(), BigDecimal.ZERO);
            result.put(proId + "_" + SpStockStatus.ONHAND_QTY.getCodeDbid(), BigDecimal.ZERO);
        }

        //Set stock info
        for (ProductStockStatus member : prodStockStatusInfos){

            if(targetStockStatusTypes.contains(member.getProductStockStatusType())){
                result.put(member.getProductId()+"_"+member.getProductStockStatusType()
                         , member.getQuantity());
            }
        }

        return result;
    }

    private BigDecimal allocateStock(String siteId
                                    , Long targetPointId
                                    , Map<String, BigDecimal> productStockMap
                                    , Long productId
                                    , BigDecimal requestQty
                                    , Map<String, BigDecimal> productStockChangedResult){

        BigDecimal result = null;
        BigDecimal onHandQty = getProductStock(productStockMap, siteId, targetPointId, productId, SpStockStatus.ONHAND_QTY.getCodeDbid());

        if(onHandQty.compareTo(requestQty) >= 0){
            result = requestQty;
        } else {
            result = onHandQty;
        }

        productStockMap = updateProductStockMap(productStockMap, siteId, targetPointId, productId, SpStockStatus.ONHAND_QTY.getCodeDbid(), BigDecimal.ZERO, result);
        productStockMap = updateProductStockMap(productStockMap, siteId, targetPointId, productId, SpStockStatus.ALLOCATED_QTY.getCodeDbid(), result, BigDecimal.ZERO);
        productStockChangedResult = registerProductStockChangedResult(productStockChangedResult, productId, SpStockStatus.ONHAND_QTY.getCodeDbid(), BigDecimal.ZERO, result);
        productStockChangedResult = registerProductStockChangedResult(productStockChangedResult, productId, SpStockStatus.ALLOCATED_QTY.getCodeDbid(), result, BigDecimal.ZERO);

        return result;
    }

    private BigDecimal getProductStock(Map<String, BigDecimal> productStockMap
                                     , String siteId
                                     , Long targetPointId
                                     , Long productId
                                     , String stockStatusTypeId){

        BigDecimal result = null;
        String prodStockKey = productId + "_" + stockStatusTypeId;
        if(productStockMap.containsKey(prodStockKey)){
            result = productStockMap.get(prodStockKey);
        }else{
            ProductStockStatus prodStockStatusInfo = inventoryManager.doGetProductStockStatusInfo(siteId
                                                                                                , targetPointId
                                                                                                , productId
                                                                                                , stockStatusTypeId);
            if(prodStockStatusInfo != null ){
                productStockMap.put(prodStockKey, prodStockStatusInfo.getQuantity());
                result = prodStockStatusInfo.getQuantity();
            }else{
                productStockMap.put(prodStockKey, BigDecimal.ZERO);
                result = BigDecimal.ZERO;
            }
        }

        return result;
    }

    private  Map<String, BigDecimal> updateProductStockMap(Map<String, BigDecimal> productStockMap
                                                         , String siteId
                                                         , Long targetPointId
                                                         , Long productId
                                                         , String productStockStatusTypeId
                                                         , BigDecimal plusQty
                                                         , BigDecimal minusQty){
        Map<String, BigDecimal> result = productStockMap;

        plusQty = plusQty == null ? BigDecimal.ZERO : plusQty;
        minusQty = minusQty == null ? BigDecimal.ZERO : minusQty;
        BigDecimal resultQty = getProductStock(result, siteId, targetPointId, productId, productStockStatusTypeId);
        resultQty = resultQty.add(plusQty).subtract(minusQty);
        String productStockKey = productId + "_" + productStockStatusTypeId;
        result.put(productStockKey, resultQty);

        return result;
    }

    private Map<Long, Long> initialSupersedingProductMap(String siteId, Set<Long> productInfoList){

        return productManager.getProductReleationInBulk(siteId
                                                      , ProductRelationClass.SUPERSEDING
                                                      , productInfoList);
    }

    private Long getSupersedingProductInfo(String siteId, Long productId, Map<Long, Long> supersedingProductInfoMap){

        Long resultId = null;

        if (productId == null ){
            return null;
        }

        if(supersedingProductInfoMap.containsKey(productId)){

            resultId = supersedingProductInfoMap.get(productId);

        }else{

            supersedingProductInfoMap.put(productId, null);
            Set<Long> proIdSet = new HashSet<>();
            proIdSet.add(productId);
            List<MstProductRelation> productRelationInfos = this.mstProductRelationRepository
                                                             .findByValidRelationType(siteId,ProductRelationClass.SUPERSEDING
                                                                                               , proIdSet
                                                                                               , DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER)
                                                                                               , DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));


            if(!productRelationInfos.isEmpty()){

                Long supersedingProId = productRelationInfos.get(0).getFromProductId()==null ? null : productRelationInfos.get(0).getFromProductId();
                supersedingProductInfoMap.put(productId, supersedingProId);
                resultId = supersedingProId;
            }

        }

        return resultId;

    }

    private String generateOrderItemKey(SalesOrderItemVO salesOrderItem){

        return  salesOrderItem.getSalesOrderId()
                      + "-" + salesOrderItem.getSeqNo()
                      + "-" + (salesOrderItem.getProductId()==null? CommonConstants.CHAR_BLANK : salesOrderItem.getProductId())
                      + "-" + (salesOrderItem.getAllocatedProductId()==null? CommonConstants.CHAR_BLANK : salesOrderItem.getAllocatedProductId());
    }

    private Map<String, SalesOrderItemVO> generateSalesOrderItemDataList(List<SalesOrderItemVO> salesOrderitemList){

        Map<String, SalesOrderItemVO> result = new HashMap<>();

        String orderItemKey = null;
        for (SalesOrderItemVO member : salesOrderitemList){

            orderItemKey = generateOrderItemKey(member);
            result.put(orderItemKey , member);

        }

        return result;
    }

    private Set<Long> initialRelationProductList(Map<String, SalesOrderItemVO> targetOrderItemInfos){

        Set<Long> result = new HashSet<>();

        for (SalesOrderItemVO member : targetOrderItemInfos.values()){

            if (member.getProductId() !=null){
                result.add(member.getProductId());
            }
            if(member.getAllocatedProductId()!=null){
                result.add(member.getAllocatedProductId());
            }
        }

        return result;
    }

    private void updateRelationProductList(Set<Long> relationProductList, Map<Long, Long> supersedingProductMap){

        Set<Long> supersededProductList = supersedingProductMap.keySet();
        for (Long member : supersededProductList){
            if(member != null ){
                relationProductList.add(member);
            }
        }

        Collection<Long> supersedingProductList = supersedingProductMap.values();
        for (Long member : supersedingProductList){
            if(member != null){
                relationProductList.add(member);
            }
        }
    }

    private List<SalesOrderItemVO> initialTargetStockAllocationOrderItemList(Map<String, SalesOrderItemVO> salesOrderItemsMapByProductKey){

        List<SalesOrderItemVO> result = new ArrayList<>();
        Collection<SalesOrderItemVO> salesOrderItemInfos = salesOrderItemsMapByProductKey.values();
        for (SalesOrderItemVO member : salesOrderItemInfos){
            BigDecimal allocateRequestQty   =member.getWaitingAllocateQty().add(member.getBoQty());
            if (allocateRequestQty.compareTo(new BigDecimal(0)) > 0){
                result.add(member);
            }
        }
        return result;
    }

    private List<SalesOrderItemVO> allocateStockForOrderItems(String siteId
                                                          , Long facilityId
                                                          , Map<Long, Long> supersedingProductInfoMap
                                                          , Map<String, BigDecimal> productStockMap
                                                          , Map<String, BigDecimal> productStockChangedResult
                                                          , Map<String, SalesOrderItemVO> salesOrderItemsMapByProductKey
                                                          , List<SalesOrderItemVO> targetOrderItemList
                                                          , Map<Long, BigDecimal> productCostMap
                                                          , String allocateDate){

        List<SalesOrderItemVO> result = new ArrayList<>();
        for (SalesOrderItemVO targetItemModel:targetOrderItemList){

            while (targetItemModel != null) {
                //Calculate allocateRequestQty & remove WaitingAllocating & BackOrder data
                BigDecimal boQty = targetItemModel.getBoQty();
                BigDecimal waitingAllocateQty = targetItemModel.getWaitingAllocateQty();
                BigDecimal allocateRequestQty = waitingAllocateQty.add(boQty);

                boolean boReleaseFlag = false;
                if (boQty != null && boQty.compareTo(BigDecimal.ZERO) > 0){
                    boReleaseFlag = true;
                }

                targetItemModel.setWaitingAllocateQty(targetItemModel.getWaitingAllocateQty().subtract(waitingAllocateQty));
                if (boReleaseFlag){
                    //remove BackOrder info from stockStatus
                    //修改减BO库存数量
                    productStockMap = cancelBoStock(productStockChangedResult, productStockMap, siteId, facilityId, targetItemModel.getAllocatedProductId(), boQty);
                    targetItemModel.setBoQty(targetItemModel.getBoQty().subtract(boQty));
                }
                //修改订单数量 减去当前预定请求数
                targetItemModel.setActualQty(targetItemModel.getActualQty().subtract(allocateRequestQty));
                targetItemModel.setOrderQty(targetItemModel.getOrderQty().subtract(allocateRequestQty));

                if(canStockAllocate(siteId, targetItemModel, productCostMap)){
                    // do stock allocate logic for product stock
                    BigDecimal allocatedResultQty = allocateStock(siteId
                                                     , facilityId
                                                     , productStockMap
                                                     , targetItemModel.getAllocatedProductId()
                                                     , allocateRequestQty
                                                     , productStockChangedResult);

                    //update AllocatedStatus for OrderItemProgress
                    targetItemModel.setAllocatedQty(targetItemModel.getAllocatedQty().add(allocatedResultQty));

                    //Update the allocateRequestQty
                    allocateRequestQty = allocateRequestQty.subtract(allocatedResultQty);

                    //update orderQty for targetItemModel&targetOrderItem
                    targetItemModel.setActualQty(targetItemModel.getActualQty().add(allocatedResultQty));
                    targetItemModel.setOrderQty(targetItemModel.getOrderQty().add(allocatedResultQty));

                    if (allocatedResultQty.signum() > 0 ){

                        result.add(targetItemModel);
                    }
                }
                // Case in allocateRequestQty > 0, the logic will be continued to find superseding product.
                if (allocateRequestQty.signum() > 0 ){
                    SalesOrderItemVO  supersedingItemModel =createSupersedingOrderItem(supersedingProductInfoMap
                                                                        , targetItemModel
                                                                        , allocateRequestQty
                                                                        , salesOrderItemsMapByProductKey
                                                                        , result);

                     if (supersedingItemModel != null){

                         targetItemModel = supersedingItemModel;
                         supersedingItemModel = null;

                     } else {

                         MstProductVO productVO = BeanMapUtils.mapTo(mstProductRepository.findByProductId(targetItemModel.getProductId()),MstProductVO.class);

                         targetItemModel.setOrderQty(targetItemModel.getOrderQty().add(allocateRequestQty));
                         targetItemModel.setActualQty(targetItemModel.getActualQty().add(allocateRequestQty));
                         Map<String,String> isUpdateBo = new HashMap<>();

                         isBoCancelByOrderItem(isUpdateBo, targetItemModel);

                         if (StringUtils.equals(isUpdateBo.get(KEY_BOFLAG), KEY_BOFLAG_BO)) {
                             isUpdateBackOrderByProduct(isUpdateBo, targetItemModel, productCostMap);
                         }

                         String cancelFlag = targetItemModel.getBoCancelFlag();
                         if (StringUtils.equals(cancelFlag, CommonConstants.CHAR_Y) || StringUtils.equals(CommonConstants.CHAR_ONE, productVO.getSalesStatusType())) {
                             isUpdateBo.put(KEY_BOFLAG,CHAR_NEGATIVE_ONE);
                         }
                         boProcess(allocateDate
                                   , targetItemModel
                                   , isUpdateBo
                                   , facilityId
                                   , allocateRequestQty
                                   , productStockMap
                                   , productStockChangedResult);

                         result.add(targetItemModel);

                         targetItemModel = null;
                     }
                } else {
                    targetItemModel = null;
                }
            }
        }
        return result;
    }

    private boolean canStockAllocate(String siteId, SalesOrderItemVO targetOrderItemInfo, Map<Long, BigDecimal> productCostMap){

        boolean result = true;
        //Cost check
        if (!isSetProductAverageCost(siteId, targetOrderItemInfo.getAllocatedProductId(), productCostMap)){
            result = false;
          }

        if ((targetOrderItemInfo.getSellingPrice() == null) || (targetOrderItemInfo.getSellingPrice().compareTo(new BigDecimal(0)) == 0)){
            result = false;
        }

        MstProduct mstProduct = this.mstProductRepository.findByProductId(targetOrderItemInfo.getAllocatedProductId());

        if(mstProduct != null&&mstProduct.getSalesStatusType().equals(SpSalesStatusType.SALESSTATUS_VALUE_NOTSALES)){
            result = false;
        }
        return result;
    }

    private SalesOrderItemVO createSupersedingOrderItem(Map<Long, Long> supersedingProductInfoMap
//                                                    , SalesOrderItem originalItemModel
                                                    , SalesOrderItemVO previousItemModel
                                                    , BigDecimal allocateRequestQty
//                                                    , BaseSalesPriceRequestModel basePriceRequestModel
                                                    , Map<String,SalesOrderItemVO> salesOrderItemsMapByProductKey
                                                    , List<SalesOrderItemVO> orderItemAllocateResultList){

        SalesOrderItemVO result = null;

        Long supersedingProId = getSupersedingProductInfo(previousItemModel.getSiteId()
                                                                 , previousItemModel.getAllocatedProductId()
                                                                 , supersedingProductInfoMap);

        if (supersedingProId != null ){

            MstProduct allocateProduct = mstProductRepository.findByProductId(supersedingProId);
            if(isCreateNewOrderItem(previousItemModel, supersedingProId)){

                SalesOrderItemVO newOrderItem = new SalesOrderItemVO();
                newOrderItem.setSalesOrderId(previousItemModel.getSalesOrderId());
                newOrderItem.setProductClassification(ProductClsType.PART.getCodeDbid());
                newOrderItem.setSettleTypeId(previousItemModel.getSettleTypeId());
                newOrderItem.setProductId(supersedingProId);
                newOrderItem.setProductCd(allocateProduct.getProductCd());
                newOrderItem.setProductNm(allocateProduct.getLocalDescription());
                newOrderItem.setAllocatedProductId(supersedingProId);
                newOrderItem.setAllocatedProductCd(allocateProduct.getProductCd());
                newOrderItem.setAllocatedProductNm(allocateProduct.getLocalDescription());
                newOrderItem.setOrderQty(allocateRequestQty);
                newOrderItem.setActualQty(allocateRequestQty);
                newOrderItem.setWaitingAllocateQty(allocateRequestQty);
                newOrderItem.setSeqNo(previousItemModel.getSeqNo());
                newOrderItem.setSiteId(previousItemModel.getSiteId());
                newOrderItem.setSellingPrice(previousItemModel.getSellingPrice());
                newOrderItem.setStandardPrice(previousItemModel.getStandardPrice());
                newOrderItem.setActualAmt(previousItemModel.getSellingPrice().multiply(allocateRequestQty));
                newOrderItem.setDiscountOffRate(previousItemModel.getDiscountOffRate());
                newOrderItem.setBoCancelFlag(previousItemModel.getBoCancelFlag());
                result = newOrderItem;
                result = findExistingSupersedingOrderItem(salesOrderItemsMapByProductKey, orderItemAllocateResultList, result);
            } else {
                result = previousItemModel;
                result.setAllocatedProductId(supersedingProId);
                result.setAllocatedProductCd(allocateProduct.getProductCd());
                result.setAllocatedProductNm(allocateProduct.getLocalDescription());
                result.setOrderQty(allocateRequestQty);
                result.setActualQty(allocateRequestQty);
                result.setWaitingAllocateQty(allocateRequestQty);
            }

        }
        return result;
    }

    private boolean isCreateNewOrderItem(SalesOrderItemVO previousItemModel, Long supersedingProId){

        boolean result = false;
        BigDecimal progressQty =previousItemModel.getAllocatedQty()
                               .add(previousItemModel.getCancelQty())
                               .add(previousItemModel.getInstructionQty())
                               .add(previousItemModel.getShipmentQty());
//
        result = progressQty.compareTo(BigDecimal.ZERO) > 0 ? true : false;
        return result;
    }

    private SalesOrderItemVO findExistingSupersedingOrderItem(Map<String,SalesOrderItemVO> salesOrderItemsMapByProductKey
                                                            , List<SalesOrderItemVO> orderItemAllocateResultList
                                                            , SalesOrderItemVO supersedingItemModel){

        SalesOrderItemVO result = null;
        for (SalesOrderItemVO member : orderItemAllocateResultList){

            if(   member.getSeqNo().equals(supersedingItemModel.getSeqNo())
                    && member.getProductId().equals(supersedingItemModel.getProductId())
                    && member.getAllocatedProductId().equals(supersedingItemModel.getAllocatedProductId())
                   ){

                     result = member;
                     result.setOrderQty(result.getOrderQty().add(supersedingItemModel.getOrderQty()));
                 }
        }
        if (result == null){
            String orderItemKey = generateOrderItemKey(supersedingItemModel);

            if (salesOrderItemsMapByProductKey.containsKey(orderItemKey)){
                result = salesOrderItemsMapByProductKey.get(orderItemKey);
                result.setOrderQty(result.getOrderQty().add(supersedingItemModel.getOrderQty()));
            }
        }

        if (result == null){
            result = supersedingItemModel;
        }

        return result;
    }

    private Map<String, BigDecimal> cancelBoStock(Map<String, BigDecimal> productStockChangedResult
                                                , Map<String, BigDecimal> productStockMap
                                                , String siteId
                                                , Long targetPointId
                                                , Long productId
                                                , BigDecimal cancelBoQty){

        Map<String, BigDecimal> result = productStockMap;

        result = updateProductStockMap(result, siteId, targetPointId, productId, SpStockStatus.BO_QTY.getCodeDbid(), BigDecimal.ZERO, cancelBoQty);

        productStockChangedResult = registerProductStockChangedResult(productStockChangedResult
                                                                    , productId
                                                                    , SpStockStatus.BO_QTY.getCodeDbid()
                                                                    , BigDecimal.ZERO
                                                                    , cancelBoQty);

        return result;
    }

    private Map<String, BigDecimal> registerProductStockChangedResult(Map<String, BigDecimal> productStockChangedResult
                                                                    , Long productId
                                                                    , String productStockStatusTypeId
                                                                    , BigDecimal plusQty
                                                                    , BigDecimal minusQty){

        Map<String, BigDecimal> result  = productStockChangedResult;

        plusQty = plusQty == null ? BigDecimal.ZERO : plusQty;
        minusQty = minusQty == null ? BigDecimal.ZERO : minusQty;

        String productStockKey = productId + "__" + productStockStatusTypeId;
        BigDecimal resultQty = BigDecimal.ZERO;
        if(result.containsKey(productStockKey)){
            resultQty = result.get(productStockKey);
        }

        resultQty = resultQty.add(plusQty).subtract(minusQty);

        result.put(productStockKey, resultQty);
        return result;
    }

    protected Map<String,String> isBoCancelByCustomer(){

        Map<String,String> result = new HashMap<String,String>();
        result.put(KEY_BOFLAG, KEY_BOFLAG_BO);

//        if (orderHeaderInfoModel.isExpenseCustomer()) {
//            result.put(KEY_BOFLAG,KEY_BOFLAG_BO_CANCEL);
//            result.put(KEY_REASON, XM03CodeInfoConstants.OrderCancelReasonTypeSub.KEY_BOCANCEL);
//        }

        return result;
    }

    protected void isBoCancelByOrderItem(Map<String,String> isUpdateBo,  SalesOrderItemVO orderItemInfo){

        // Reset
        isUpdateBo.put(KEY_BOFLAG, KEY_BOFLAG_BO);
        if(CommonConstants.CHAR_Y
                   .equals(orderItemInfo.getBoCancelFlag())){
                 isUpdateBo.put(KEY_BOFLAG,KEY_BOFLAG_BO_CANCEL);
                 isUpdateBo.put(KEY_REASON, OrderCancelReasonTypeSub.KEY_BOCANCEL);
        }

    }

    protected void isUpdateBackOrderByProduct(Map<String,String> isUpdateBo,  SalesOrderItemVO orderItemInfo, Map<Long, BigDecimal> productCostMap){

        isUpdateBo = returnCostResult(isUpdateBo, orderItemInfo,COST_BO_OR_CANCEL_SIGN , productCostMap);//Xm03GeneralConstants.COST_BO_OR_CANCEL_SIGN
        if (StringUtils.equals(isUpdateBo.get(KEY_BOFLAG), KEY_BOFLAG_BO_CANCEL)) {
            return;
        }
        isUpdateBo = returnPriceResult(isUpdateBo, orderItemInfo, PRICE_BO_OR_CANCEL_SIGN);
        if (StringUtils.equals(isUpdateBo.get(KEY_BOFLAG), KEY_BOFLAG_BO_CANCEL)) {
            return;
        }
        MstProduct product = this.mstProductRepository.findByProductId(orderItemInfo.getAllocatedProductId());
        if (product.getSalesStatusType().equals(SpSalesStatusType.SALESSTATUS_VALUE_NOTSALES)
         || product.getSalesStatusType().equals(SpSalesStatusType.SALESSTATUS_VALUE_STOCKONLY)
            ){
            isUpdateBo.put(KEY_BOFLAG,KEY_BOFLAG_BO_CANCEL);
            isUpdateBo.put(KEY_REASON, OrderCancelReasonTypeSub.KEY_NOTSALECANCEL);
        }
    }

    private Map<String,String> returnCostResult(Map<String,String> result, SalesOrderItemVO orderItemInfo, String flag, Map<Long, BigDecimal> productCostMap) {

        if (!isSetProductAverageCost(orderItemInfo.getSiteId(), orderItemInfo.getAllocatedProductId(), productCostMap)){
            result.put(KEY_BOFLAG,flag);
            result.put(KEY_REASON, OrderCancelReasonTypeSub.KEY_COSTZEROCANCEL);
        }

        return result;
    }

    private Map<String,String> returnPriceResult(Map<String,String> result, SalesOrderItemVO orderItemInfo, String flag) {

        if ((orderItemInfo.getSellingPrice() == null) || (orderItemInfo.getSellingPrice().compareTo(new BigDecimal(0)) == 0)){
            result.put(KEY_BOFLAG,flag);
            result.put(KEY_REASON, OrderCancelReasonTypeSub.KEY_PRICEZEROCANCEL);
        }

        return result;
    }

    private void boProcess(String allocateDueDate
                               , SalesOrderItemVO targetItemModel
                               , Map<String, String> isUpdateBo
                               , Long targetPointId
                               , BigDecimal allocateRequestQty
                               , Map<String, BigDecimal> productStockMap
                               , Map<String, BigDecimal> productStockChangedResult) {

        if (KEY_BOFLAG_BO.equals(isUpdateBo.get(KEY_BOFLAG))){
            //update boQty
            targetItemModel.setBoQty(targetItemModel.getBoQty().add(allocateRequestQty));

            productStockChangedResult = registerProductStockChangedResult(productStockChangedResult
                                                                        , targetItemModel.getAllocatedProductId()
                                                                        , SpStockStatus.BO_QTY.getCodeDbid()
                                                                        , allocateRequestQty
                                                                        , BigDecimal.ZERO);

            productStockMap = updateProductStockMap(productStockMap
                                                , targetItemModel.getSiteId()
                                                , targetPointId
                                                , targetItemModel.getAllocatedProductId()
                                                , SpStockStatus.BO_QTY.getCodeDbid()
                                                , allocateRequestQty
                                                , BigDecimal.ZERO);
        } else {
            /***BO Cacnel **/
            //update cancel Qty
            targetItemModel.setCancelQty(targetItemModel.getCancelQty().add(allocateRequestQty));
            targetItemModel.setActualQty((targetItemModel.getActualQty()).subtract(allocateRequestQty));
            BigDecimal roundedSellingPrice = targetItemModel.getSellingPrice().setScale(0, RoundingMode.HALF_UP);
            BigDecimal roundedSellingPriceNotVat = targetItemModel.getSellingPriceNotVat().setScale(0, RoundingMode.HALF_UP);
            targetItemModel.setActualAmt(roundedSellingPrice.multiply(targetItemModel.getActualQty()));
            targetItemModel.setActualAmtNotVat(roundedSellingPriceNotVat.multiply(targetItemModel.getActualQty()));
            //Setting the reason of order cancel
            if(isUpdateBo.get(KEY_REASON)!=null){
                targetItemModel.setCancelReasonType(isUpdateBo.get(KEY_REASON));
            }
            salesOrderManager.updateProductOrderResultSummary(allocateDueDate
                                                            , targetPointId
                                                            , targetItemModel.getAllocatedProductId()
                                                            , targetItemModel.getSiteId(),allocateRequestQty);
        }
    }

    public Map<Long, BigDecimal> getPartsStockMap(String siteId, Long facilityId, Set<Long> targetProductIds){

        List<ProductStockStatus> pssList = productStockStatusRepository.findStockStatusProductList(siteId
                                                                                                 , facilityId
                                                                                                 , targetProductIds
                                                                                                 , ProductClsType.PART.getCodeDbid()
                                                                                                 , PJConstants.SpStockStatus.ONHAND_QTY.getCodeDbid()
                                                                                                  );

        return pssList.stream().collect(Collectors.toMap(ProductStockStatus::getProductId, ProductStockStatus::getQuantity));
    }

    public void doBoRelease(String siteId, Long deliveryPointId, List<TargetSalesOrderItemBO> targetList){

        if (targetList.isEmpty()){return;}

        //数据集合化
        Set<Long> relationProductIds = this.getRelationProductIds(targetList);

        Map<Long, Long> productSupersededRelationMap = productManager.getProductSupersededRelationMap(relationProductIds);
        relationProductIds.addAll(productSupersededRelationMap.values());

        //获取所有部品的信息（库存：OnHand，销售类型）
        Map<Long, BigDecimal> partsStockMap = this.getPartsStockMap(siteId, deliveryPointId, relationProductIds);
        Map<Long, PartsInfoBO> partsInfoMap = mstProductRepository.listProductInfoForAllocate(relationProductIds).stream().collect(Collectors.toMap(PartsInfoBO::getPartsId, t -> t));

        //二次过滤： 有库存 或 存在替代件才进入处理逻辑
        Map<Long, List<SalesOrderItem>> boReleaseGroup = this.generateBoReleaseGroup(targetList, partsStockMap, productSupersededRelationMap, partsInfoMap);

        if (boReleaseGroup.isEmpty()){return;}

        List<ProductCost> productCostList = productCostRepository.findByProductIdInAndCostTypeAndSiteId(relationProductIds, CostType.AVERAGE_COST, siteId);
        Map<Long, BigDecimal> productAvgCost = productCostList.stream().collect(Collectors.toMap(ProductCost::getProductId, ProductCost::getCost, (pre,next) -> pre));

        SalesOrderItem targetSalesOrderItem;
        boolean partsSkipFlag;

        Map<String, SalesOrderItem> salesOrderItemForUpdateMap = new HashMap<>();
        Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap = new HashMap<>();

        for (Entry<Long, List<SalesOrderItem>> boReleaseEntry : boReleaseGroup.entrySet()){

            partsSkipFlag = false;

            //按优先级处理item
            for (SalesOrderItem releaseOrderBO : boReleaseEntry.getValue()){

                targetSalesOrderItem = this.getTargetSalesOrderItem(releaseOrderBO, salesOrderItemForUpdateMap, true);

                partsSkipFlag = this.allocateSalesOrderItem(targetSalesOrderItem
                                                          , siteId
                                                          , deliveryPointId
                                                          , CommonConstants.FLAG_UNACTIVE
                                                          , true
                                                          , partsStockMap
                                                          , partsInfoMap
                                                          , productAvgCost
                                                          , productSupersededRelationMap
                                                          , salesOrderItemForUpdateMap
                                                          , stockStatusVOChangeMap
                                                          , null);

                if (partsSkipFlag){
                    break;
                }
            }
        }

        //库存处理
        inventoryManager.updateProductStockStatusByMap(stockStatusVOChangeMap);

        if (salesOrderItemForUpdateMap.isEmpty()){return;}

        this.updateSalesOrderAndItems(siteId, salesOrderItemForUpdateMap);

    }

    /**
     * @param siteId
     * @param salesOrderItemForUpdateMap
     */
    private void updateSalesOrderAndItems(String siteId, Map<String, SalesOrderItem> salesOrderItemForUpdateMap) {

        List<SalesOrderItem> salesOrderItemForUpdateList = new ArrayList<>();

        SalesOrderItem salesOrderItemForUpdate;
        SalesOrderItem salesOrderItemForExist;
        String key;
        Integer seqNo;

        List<SalesOrderItem> salesOrderItemForAll = salesOrderItemRepository.findBySalesOrderIds(siteId, salesOrderItemForUpdateMap.values().stream().map(SalesOrderItem::getSalesOrderId).collect(Collectors.toSet()));
        Map<String, SalesOrderItem> salesOrderItemForExistMap = salesOrderItemForAll.stream().collect(Collectors.toMap(k -> (ObjectUtils.isEmpty(k.getSalesOrderId()) ? CommonConstants.CHAR_BLANK : k.getSalesOrderId().toString()) + "-" + (ObjectUtils.isEmpty(k.getProductId()) ? CommonConstants.CHAR_BLANK : k.getProductId().toString()) + "-" + (ObjectUtils.isEmpty(k.getAllocatedProductId()) ? CommonConstants.CHAR_BLANK : k.getAllocatedProductId().toString()), k -> k));
        Map<Long, Integer> salesOrderMaxSeqMap = salesOrderItemForAll.stream().collect(Collectors.toMap(SalesOrderItem::getSalesOrderId, SalesOrderItem :: getSeqNo, (o1, o2) -> o1.compareTo(o2)> 0 ? o1 : o2));

        for (Entry<String, SalesOrderItem> salesOrderItemEntry : salesOrderItemForUpdateMap.entrySet()){

            salesOrderItemForUpdate = salesOrderItemEntry.getValue();
            key = salesOrderItemEntry.getKey();

            //在DB中存在的Item
            if (!ObjectUtils.isEmpty(salesOrderItemForUpdate.getSeqNo())){

                if (salesOrderItemForUpdate.getOrderQty().signum() > 0){

                    salesOrderItemForUpdateList.add(salesOrderItemForUpdate);
                    salesOrderItemForExistMap.put(key, salesOrderItemForUpdate);
                }
            }
            //新替代的Item
            else {

                //判断Item在原order中是否已存在
                salesOrderItemForExist = salesOrderItemForExistMap.get(key);

                if (!ObjectUtils.isEmpty(salesOrderItemForExist)){

                    salesOrderItemForExist = this.superposeSalesOrderItem(salesOrderItemForExist, salesOrderItemForUpdate);

                    salesOrderItemForUpdateList.add(salesOrderItemForExist);
                    salesOrderItemForExistMap.put(key, salesOrderItemForExist);
                }
                else if (salesOrderItemForUpdate.getOrderQty().signum() > 0){

                    seqNo = salesOrderMaxSeqMap.get(salesOrderItemForUpdate.getSalesOrderId()) + 1;
                    salesOrderItemForUpdate.setSeqNo(seqNo);
                    salesOrderMaxSeqMap.put(salesOrderItemForUpdate.getSalesOrderId(), seqNo);

                    salesOrderItemForUpdateList.add(salesOrderItemForUpdate);
                    salesOrderItemForExistMap.put(key, salesOrderItemForUpdate);
                }
            }
        }

        if (!salesOrderItemForUpdateList.isEmpty()) {
            salesOrderItemRepository.saveAll(salesOrderItemForUpdateList);
        }

        //更新订单状态+重计算总金额
        this.updateSalesOrderInfo(salesOrderItemForUpdateMap, salesOrderItemForExistMap);
    }

    /**
     * @param salesOrderItemForUpdateMap
     * @param salesOrderItemForExistMap
     */
    private void updateSalesOrderInfo(Map<String, SalesOrderItem> salesOrderItemForUpdateMap, Map<String, SalesOrderItem> salesOrderItemForExistMap) {

        List<SalesOrder> salesOrderList = salesOrderRepository.findBySalesOrderIdIn(salesOrderItemForUpdateMap.values().stream().map(SalesOrderItem::getSalesOrderId).collect(Collectors.toSet()));

        Map<Long, List<SalesOrderItem>> salesOrderItemGroup = salesOrderItemForExistMap.values().stream().collect(Collectors.groupingBy(SalesOrderItem::getSalesOrderId));

        for (SalesOrder salesOrder : salesOrderList){

            salesOrder.setOrderStatus(this.getSalesOrderCurrentStatus(salesOrderItemGroup.get(salesOrder.getSalesOrderId())));
            this.calculateTotalDataForSalesOrder(salesOrder, salesOrderItemGroup.get(salesOrder.getSalesOrderId()));

        }

        salesOrderRepository.saveAll(salesOrderList);
    }

    /**
     * @param targetList
     * @return
     */
    private Set<Long> getRelationProductIds(List<TargetSalesOrderItemBO> targetList) {Set<Long> relationProductIds = new HashSet<>();

        targetList.forEach(targetSalesOrderItemBO -> {
            if (!ObjectUtils.isEmpty(targetSalesOrderItemBO.getProductId())) {
                relationProductIds.add(targetSalesOrderItemBO.getProductId());
            }
            if (!ObjectUtils.isEmpty(targetSalesOrderItemBO.getAllocateProdId())) {
                relationProductIds.add(targetSalesOrderItemBO.getAllocateProdId());
            }
        });
        return relationProductIds;
    }

    public String getSalesOrderCurrentStatus(List<SalesOrderItem> salesOrderItemList){

        String result = MstCodeConstants.SalesOrderStatus.SP_PROFORMA;

        boolean hasBoQty = false;
        boolean hasAllocatedQty = false;
        boolean hasInstructionQty = false;
        boolean hasShipmentQty = false;
        boolean hasCancelQty = false;

        for (SalesOrderItem salesOrderItem : salesOrderItemList) {

            hasBoQty = hasBoQty ? hasBoQty : salesOrderItem.getBoQty().signum() > 0;
            hasAllocatedQty = hasAllocatedQty ? hasAllocatedQty : salesOrderItem.getAllocatedQty().signum() > 0;
            hasInstructionQty = hasInstructionQty ? hasInstructionQty : salesOrderItem.getInstructionQty().signum() > 0;
            hasShipmentQty = hasShipmentQty ? hasShipmentQty : salesOrderItem.getShipmentQty().signum() > 0;
            hasCancelQty = hasCancelQty ? hasCancelQty : salesOrderItem.getCancelQty().signum() > 0;
        }

        if (hasBoQty){

            result = MstCodeConstants.SalesOrderStatus.SP_WAITINGALLOCATE;
        }
        else if (hasAllocatedQty){

            result = MstCodeConstants.SalesOrderStatus.SP_WAITINGPICKING;//PJConstants.SalesOrderActionType.STOCK_ALLOCATE;
        }
        else if (hasInstructionQty){

            result = MstCodeConstants.SalesOrderStatus.SP_ONPICKING;//PJConstants.SalesOrderActionType.PICKING_INSTRUCTION;
        }
        else if (hasShipmentQty){

            result = MstCodeConstants.SalesOrderStatus.SP_SHIPMENTED;//PJConstants.SalesOrderActionType.SHIPPING_COMPLETION;
        }
        else if (hasCancelQty){

            result = MstCodeConstants.SalesOrderStatus.SP_CANCELLED;//PJConstants.SalesOrderActionType.ORDER_CANCEL;
        }

        return result;
    }

    public SalesOrder calculateTotalDataForSalesOrder(SalesOrder salesOrderInfo, List<SalesOrderItem> salesOrderItemList){

        BigDecimal totalActualQty = CommonConstants.BIGDECIMAL_ZERO;
        BigDecimal totalActualAmt = CommonConstants.BIGDECIMAL_ZERO;

        for (SalesOrderItem salesOrderItem : salesOrderItemList) {

            totalActualQty = totalActualQty.add(salesOrderItem.getActualQty());
            totalActualAmt = totalActualAmt.add(salesOrderItem.getActualAmt());
        }

        salesOrderInfo.setTotalActualQty(totalActualQty);
        salesOrderInfo.setTotalActualAmt(totalActualAmt);

        return salesOrderInfo;
    }

    private Map<Long, List<SalesOrderItem>> generateBoReleaseGroup(List<TargetSalesOrderItemBO> targetList
                                                                 , Map<Long, BigDecimal> partsStockMap
                                                                 , Map<Long, Long> productSupersededRelationMap
                                                                 , Map<Long, PartsInfoBO> partsInfoMap){

        Map<Long, List<SalesOrderItem>> result = new HashMap<>();

        //如果当前部品有库存，或者有可销售的替代件
        List<TargetSalesOrderItemBO> filterList = this.filterBoReleaseTargetList(targetList, partsStockMap, productSupersededRelationMap, partsInfoMap);

        if (filterList.isEmpty()){return result;}

        Map<Long, SalesOrderItem> salesOrderItemMap = salesOrderItemRepository.findBySalesOrderItemIdIn(
                                                                                                        filterList.stream().map(TargetSalesOrderItemBO::getSalesOrderItemId).collect(Collectors.toSet()))
                                                                                                       .stream().collect(Collectors.toMap(SalesOrderItem::getSalesOrderItemId, t -> t));

        SalesOrderItem salesOrderItem;
        List<SalesOrderItem> value;

        for (TargetSalesOrderItemBO model : filterList){

            salesOrderItem = salesOrderItemMap.get(model.getSalesOrderItemId());

            if (ObjectUtils.isEmpty(salesOrderItem)){continue;}

            value = result.containsKey(model.getAllocateProdId()) ? result.get(model.getAllocateProdId()) : new ArrayList<>();

            value.add(salesOrderItem);

            result.put(model.getAllocateProdId(), value);
        }

        return result;
    }

    public SalesOrderItem getTargetSalesOrderItem(SalesOrderItem salesOrderItem, Map<String, SalesOrderItem> salesOrderItemForUpdateMap, boolean keyContainSalesOrderIdFlag){

        String key = keyContainSalesOrderIdFlag
                   ? this.generateOrderItemKey(salesOrderItem.getSalesOrderId(), salesOrderItem.getProductId(), salesOrderItem.getAllocatedProductId())
                   : this.generateOrderItemKey(salesOrderItem.getProductId(), salesOrderItem.getAllocatedProductId());

        //如果已经经过替代处理了，则需要叠加上之前处理的结果。
        return salesOrderItemForUpdateMap.containsKey(key) ? this.superposeSalesOrderItem(salesOrderItem, salesOrderItemForUpdateMap.get(key)) : salesOrderItem;
    }

    private List<TargetSalesOrderItemBO> filterBoReleaseTargetList(List<TargetSalesOrderItemBO> targetList
                                                                 , Map<Long, BigDecimal> partsStockMap
                                                                 , Map<Long, Long> productSupersededRelationMap
                                                                 , Map<Long, PartsInfoBO> partsInfoMap){

        List<TargetSalesOrderItemBO> result = new ArrayList<>();
        Long tmpProductId = null;

        //必须按allocatedProductId排序
        for (TargetSalesOrderItemBO model : targetList){

            BigDecimal onHandQty = BigDecimal.ZERO;
            if (!model.getAllocateProdId().equals(tmpProductId)){

                tmpProductId = model.getAllocateProdId();
                onHandQty = partsStockMap.get(tmpProductId);

                partsStockMap.put(tmpProductId, onHandQty);

            }
            //如果当前部品有库存，或者有可销售的替代件时，则需要处理
            if (onHandQty.signum() > 0 || !ObjectUtils.isEmpty(this.getSupersedingPartsWithSalesStatus(productSupersededRelationMap, model.getAllocateProdId(), partsInfoMap))){

                result.add(model);
            }
        }

        result.sort(Comparator.comparing(TargetSalesOrderItemBO::getOrderPriorityType)
                                        .thenComparingInt(TargetSalesOrderItemBO::getPrioritySeqNo).reversed()
                                        .thenComparing(TargetSalesOrderItemBO::getAllocateDueDate));

        return result;
    }

    public PartsInfoBO getSupersedingPartsWithSalesStatus(Map<Long, Long> productSupersededRelationMap, Long targetPartsId, Map<Long, PartsInfoBO> partsInfoMap){

        PartsInfoBO result = null;
        Long supersedingPartsId = null;

        do {
            targetPartsId = ObjectUtils.isEmpty(result) ? targetPartsId : result.getPartsId();

            supersedingPartsId = productSupersededRelationMap.get(targetPartsId);

            result = supersedingPartsId == null ? null : partsInfoMap.get(supersedingPartsId);

        } while(result != null && StringUtils.equals(result.getSalesStatusType(), PJConstants.SpSalesStatusType.SALESSTATUS_VALUE_NOTSALES.getCodeDbid()));

        return result;
    }

    private String generateOrderItemKey(Long salesOrderId, Long fromProductId, Long allocatedProductId){

        return (ObjectUtils.isEmpty(salesOrderId) ? CommonConstants.CHAR_BLANK : salesOrderId.toString())
             + "-"
             + (ObjectUtils.isEmpty(fromProductId) ? CommonConstants.CHAR_BLANK : fromProductId.toString())
             + "-"
             + (ObjectUtils.isEmpty(allocatedProductId) ? CommonConstants.CHAR_BLANK : allocatedProductId.toString());
    }

    private String generateOrderItemKey(Long fromProductId, Long allocatedProductId){

        return (ObjectUtils.isEmpty(fromProductId) ? CommonConstants.CHAR_BLANK : fromProductId.toString())
             + "-"
             + (ObjectUtils.isEmpty(allocatedProductId) ? CommonConstants.CHAR_BLANK : allocatedProductId.toString());
    }

    public SalesOrderItem superposeSalesOrderItem(SalesOrderItem baseItem, SalesOrderItem addItem){

        //可能存在原数据全部被cancel的情况，这时相关数据必须重新刷新
        if (baseItem.getActualQty().signum() == 0){

            baseItem.setSpecialPrice(addItem.getSpecialPrice());
            baseItem.setSellingPrice(addItem.getSellingPrice());
            baseItem.setDiscountOffRate(addItem.getDiscountOffRate());
            baseItem.setOrderPrioritySeq(addItem.getOrderPrioritySeq());
            baseItem.setSvSellingPrice(addItem.getSvSellingPrice());
            baseItem.setSvClaimFlag(addItem.getSvClaimFlag());
        }

        baseItem.setActualQty(baseItem.getActualQty().add(addItem.getActualQty()));
        baseItem.setActualAmt(baseItem.getActualAmt().add(addItem.getActualAmt()));
        baseItem.setAllocatedQty(baseItem.getAllocatedQty().add(addItem.getAllocatedQty()));
        baseItem.setBoQty(baseItem.getBoQty().add(addItem.getBoQty()));
        baseItem.setCancelQty(baseItem.getCancelQty().add(addItem.getCancelQty()));
        baseItem.setOrderQty(baseItem.getOrderQty().add(addItem.getOrderQty()));
        baseItem.setWaitingAllocateQty(baseItem.getWaitingAllocateQty().add(addItem.getWaitingAllocateQty()));

        return baseItem;
    }

    private BigDecimal getOnHandStockQty(Map<Long, BigDecimal> partsStockMap, Long productId){

        BigDecimal result = partsStockMap.get(productId);

        if (result == null){

            result = CommonConstants.BIGDECIMAL_ZERO;
            partsStockMap.put(productId, result);
        }

        return result;
    }

    public boolean allocateSalesOrderItem(SalesOrderItem targetSoItem
                                        , String siteId
                                        , Long facilityId
                                        , String boCancelSign
                                        , boolean keyContainSalesOrderIdFlag
                                        , Map<Long, BigDecimal> partsStockMap
                                        , Map<Long, PartsInfoBO> partsInfoMap
                                        , Map<Long, BigDecimal> productAvgCost
                                        , Map<Long, Long> productSupersededRelationMap
                                        , Map<String, SalesOrderItem> salesOrderItemForUpdateMap
                                        , Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap
                                        , SalesOrder salesOrder){

        //如果为非卖品 、平均成本为0时，库存数量替换为0
        BigDecimal onhandStockQty = partsInfoMap.get(targetSoItem.getAllocatedProductId()).getSalesStatusType().equals(PJConstants.SpSalesStatusType.SALESSTATUS_VALUE_NOTSALES.getCodeDbid())
                                                 || !productAvgCost.containsKey(targetSoItem.getAllocatedProductId())
                                                 || productAvgCost.get(targetSoItem.getAllocatedProductId()).equals(CommonConstants.BIGDECIMAL_ZERO)
                                    ? BigDecimal.ZERO : this.getOnHandStockQty(partsStockMap, targetSoItem.getAllocatedProductId());

        //处理waitingAllocateQty, 库存不足则转为BO
        if (targetSoItem.getWaitingAllocateQty().signum() > 0){
            onhandStockQty = this.reduceWaitingAllocate(targetSoItem, onhandStockQty, siteId, facilityId, stockStatusVOChangeMap, partsInfoMap.get(targetSoItem.getAllocatedProductId()).getSalLotSize());
        }

        //处理BoQty
        if (onhandStockQty.signum() > 0 && targetSoItem.getBoQty().signum() > 0){
            onhandStockQty = this.reduceBo(targetSoItem, onhandStockQty, siteId, facilityId, stockStatusVOChangeMap);
        }
        //onhandQty放回map
        partsStockMap.put(targetSoItem.getAllocatedProductId(), onhandStockQty);

        //仍有BO时，查找可售卖的替代件
        if (targetSoItem.getBoQty().signum() > 0){

            PartsInfoBO supersedingParts = this.getSupersedingPartsWithSalesStatus(productSupersededRelationMap, targetSoItem.getAllocatedProductId(), partsInfoMap);

            //不存在可销售得替代件
            if (ObjectUtils.isEmpty(supersedingParts)){

                PartsInfoBO targetParts = partsInfoMap.get(targetSoItem.getAllocatedProductId());

                //[选中了缺货取消]或[非卖]或[只卖库存], 则预定不足部分直接取消
                if (StringUtils.equals(boCancelSign, CommonConstants.FLAG_ACTIVE)
                        || StringUtils.equals(targetParts.getSalesStatusType(), PJConstants.SpSalesStatusType.SALESSTATUS_VALUE_NOTSALES.getCodeDbid())
                        || StringUtils.equals(targetParts.getSalesStatusType(), PJConstants.SpSalesStatusType.SALESSTATUS_VALUE_STOCKONLY.getCodeDbid())){

                    targetSoItem = this.boCancel(targetSoItem, siteId, facilityId, stockStatusVOChangeMap);
                }

                this.addSalesOrderItem2Map(salesOrderItemForUpdateMap, targetSoItem, keyContainSalesOrderIdFlag);

                //非新增数据，没有onhandQty，没有替代件，依然存在BO，则后续相同parts的salesOrderItem不需要再处理，直接跳出外层循环(仅库存解消用)
                if (!ObjectUtils.isEmpty(targetSoItem.getSeqNo()) && targetSoItem.getBoQty().signum() > 0 ) {return true;}
                return false;
            }
            else {

                //存在可销售替代件，则生成新的targetItem, 并再次进入循环
                return this.allocateSalesOrderItem(this.generateSupersedingSalesOrderItem(targetSoItem
                                                                                        , siteId
                                                                                        , facilityId
                                                                                        , keyContainSalesOrderIdFlag
                                                                                        , supersedingParts
                                                                                        , stockStatusVOChangeMap
                                                                                        , salesOrderItemForUpdateMap)
                                                      , siteId
                                                      , facilityId
                                                      , boCancelSign
                                                      , keyContainSalesOrderIdFlag
                                                      , partsStockMap
                                                      , partsInfoMap
                                                      , productAvgCost
                                                      , productSupersededRelationMap
                                                      , salesOrderItemForUpdateMap
                                                      , stockStatusVOChangeMap
                                                      , salesOrder);
                }
            }
            else {

            //没有BO时，本条Item处理结束
            this.addSalesOrderItem2Map(salesOrderItemForUpdateMap, targetSoItem, keyContainSalesOrderIdFlag);
            return false;
        }
    }

    private void addSalesOrderItem2Map(Map<String, SalesOrderItem> salesOrderItemMap, SalesOrderItem targetSoItem, boolean keyContainSalesOrderIdFlag){

        salesOrderItemMap.put(keyContainSalesOrderIdFlag
                            ? this.generateOrderItemKey(targetSoItem.getSalesOrderId(), targetSoItem.getProductId(), targetSoItem.getAllocatedProductId())
                            : this.generateOrderItemKey(targetSoItem.getProductId(), targetSoItem.getAllocatedProductId()), targetSoItem);
    }

    private SalesOrderItem boCancel(SalesOrderItem targetSoItem, String siteId, Long facilityId, Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap){

        BigDecimal targetQty = targetSoItem.getBoQty().add(targetSoItem.getWaitingAllocateQty());

        targetSoItem.setCancelQty(targetSoItem.getCancelQty().add(targetQty));
        targetSoItem.setActualQty(targetSoItem.getActualQty().subtract(targetQty));
        targetSoItem.setActualAmt(targetSoItem.getActualQty().multiply(targetSoItem.getSellingPrice()).setScale(2, RoundingMode.HALF_UP));

        //BO-
        if (targetQty.signum() > 0){

            inventoryManager.generateStockStatusVOMap(siteId, facilityId, targetSoItem.getAllocatedProductId(), targetQty.negate(), PJConstants.SpStockStatus.BO_QTY.getCodeDbid(), stockStatusVOChangeMap);
        }

        targetSoItem.setWaitingAllocateQty(CommonConstants.BIGDECIMAL_ZERO);
        targetSoItem.setBoQty(CommonConstants.BIGDECIMAL_ZERO);

        return targetSoItem;
    }

    private BigDecimal reduceBo(SalesOrderItem targetSalesOrderItem, BigDecimal onhandStockQty, String siteId, Long facilityId, Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap){

        BigDecimal targetQty = (onhandStockQty.compareTo(targetSalesOrderItem.getBoQty()) > 0) ? targetSalesOrderItem.getBoQty() : onhandStockQty;

        onhandStockQty = onhandStockQty.subtract(targetQty);

        targetSalesOrderItem.setBoQty(targetSalesOrderItem.getBoQty().subtract(targetQty));
        targetSalesOrderItem.setAllocatedQty(targetSalesOrderItem.getAllocatedQty().add(targetQty));

        //OnHand-; BO-; Allocate+
        if (targetQty.signum() > 0){

            inventoryManager.generateStockStatusVOMap(siteId, facilityId, targetSalesOrderItem.getAllocatedProductId(), targetQty.negate(), PJConstants.SpStockStatus.ONHAND_QTY.getCodeDbid(), stockStatusVOChangeMap);
            inventoryManager.generateStockStatusVOMap(siteId, facilityId, targetSalesOrderItem.getAllocatedProductId(), targetQty.negate(), PJConstants.SpStockStatus.BO_QTY.getCodeDbid(), stockStatusVOChangeMap);
            inventoryManager.generateStockStatusVOMap(siteId, facilityId, targetSalesOrderItem.getAllocatedProductId(), targetQty, PJConstants.SpStockStatus.ALLOCATED_QTY.getCodeDbid(), stockStatusVOChangeMap);

        }

        return onhandStockQty;
    }

    private BigDecimal reduceWaitingAllocate(SalesOrderItem targetSoItem
                                            , BigDecimal onhandStockQty
                                            , String siteId
                                            , Long facilityId
                                            , Map<String
                                            , Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap
                                            , BigDecimal salesLotSize){

        BigDecimal targetQty = onhandStockQty.compareTo(targetSoItem.getWaitingAllocateQty()) > 0 ? targetSoItem.getWaitingAllocateQty() : onhandStockQty;
        BigDecimal toBoQty = targetSoItem.getWaitingAllocateQty().subtract(targetQty);

        onhandStockQty = onhandStockQty.subtract(targetQty);

        targetSoItem.setBoQty(targetSoItem.getBoQty().add(toBoQty));
        targetSoItem.setAllocatedQty(targetSoItem.getAllocatedQty().add(targetQty));
        targetSoItem.setWaitingAllocateQty(CommonConstants.BIGDECIMAL_ZERO);

        //OnHand-; Allocate+
        if (targetQty.signum() > 0){
            inventoryManager.generateStockStatusVOMap(siteId, facilityId, targetSoItem.getAllocatedProductId(), targetQty.negate(), PJConstants.SpStockStatus.ONHAND_QTY.getCodeDbid(), stockStatusVOChangeMap);
            inventoryManager.generateStockStatusVOMap(siteId, facilityId, targetSoItem.getAllocatedProductId(), targetQty, PJConstants.SpStockStatus.ALLOCATED_QTY.getCodeDbid(), stockStatusVOChangeMap);

        }
        //BO+
        if (toBoQty.signum() > 0){
            inventoryManager.generateStockStatusVOMap(siteId, facilityId, targetSoItem.getAllocatedProductId(), toBoQty, PJConstants.SpStockStatus.BO_QTY.getCodeDbid(), stockStatusVOChangeMap);
        }

        return onhandStockQty;
    }

    private SalesOrderItem generateSupersedingSalesOrderItem(SalesOrderItem targetSoItem
                                                           , String siteId
                                                           , Long facilityId
                                                           , boolean keyContainSalesOrderIdFlag
                                                           , PartsInfoBO supersedingParts
                                                           , Map<String, Map<Long, ProductStockStatusVO>> stockStatusVOChangeMap
                                                           , Map<String, SalesOrderItem> salesOrderItemForUpdateMap){

        SalesOrderItem result;
        BigDecimal targetQty = targetSoItem.getBoQty().add(targetSoItem.getWaitingAllocateQty());
        SalesOrderItem salesOrderItemAlreadyUpdated = salesOrderItemForUpdateMap.get(keyContainSalesOrderIdFlag ?
                                                               this.generateOrderItemKey(targetSoItem.getSalesOrderId(), targetSoItem.getProductId(), supersedingParts.getPartsId())
                                                             : this.generateOrderItemKey(targetSoItem.getProductId(), supersedingParts.getPartsId()));

        if (ObjectUtils.isEmpty(salesOrderItemAlreadyUpdated)){

            result = new SalesOrderItem();

            result.setSiteId(targetSoItem.getSiteId());
            result.setSalesOrderId(targetSoItem.getSalesOrderId());
            result.setProductId(targetSoItem.getProductId());
            result.setProductNm(targetSoItem.getProductNm());
            result.setProductClassification(targetSoItem.getProductClassification());
            result.setDiscountOffRate(targetSoItem.getDiscountOffRate());
            result.setProductCd(targetSoItem.getProductCd());
            result.setAllocatedProductId(supersedingParts.getPartsId());
            result.setAllocatedProductCd(supersedingParts.getPartsNo());
            result.setAllocatedProductNm(supersedingParts.getPartsNm());
            result.setOrderPrioritySeq(targetSoItem.getOrderPrioritySeq());
            result.setStandardPrice(supersedingParts.getPrice());
            result.setSpecialPrice(targetSoItem.getSpecialPrice());
            result.setSvSellingPrice(targetSoItem.getSvSellingPrice());
            result.setSvClaimFlag(targetSoItem.getSvClaimFlag());

            if((targetSoItem.getDiscountOffRate().compareTo(BigDecimal.ZERO)>0)
                    && (targetSoItem.getDiscountOffRate().compareTo(new BigDecimal(100)) > 0)
                    || targetSoItem.getSpecialPrice().compareTo(BigDecimal.ZERO)>0){

                result.setSellingPrice(targetSoItem.getSellingPrice());
            }else{

                result.setSellingPrice(supersedingParts.getPrice());
            }

            result.setOrderQty(targetQty);
            result.setBoQty(targetQty);
            result.setActualQty(targetQty);
            result.setActualAmt(result.getActualQty().multiply(result.getSellingPrice()).setScale(2, RoundingMode.HALF_UP));
        }
        else {

            result = salesOrderItemAlreadyUpdated;

            //可能存在原数据全部被cancel的情况，这时相关数据必须重新刷新
            if (result.getActualQty().signum() == 0){

                result.setSpecialPrice(targetSoItem.getSpecialPrice());
                result.setSellingPrice(targetSoItem.getSellingPrice());
                result.setDiscountOffRate(targetSoItem.getDiscountOffRate());
                result.setOrderPrioritySeq(targetSoItem.getOrderPrioritySeq());
                result.setSvSellingPrice(targetSoItem.getSvSellingPrice());
                result.setSvClaimFlag(targetSoItem.getSvClaimFlag());
            }

            result.setOrderQty(result.getOrderQty().add(targetQty));
            result.setBoQty(result.getBoQty().add(targetQty));
            result.setActualQty(result.getActualQty().add(targetQty));
            result.setActualAmt(result.getActualQty().multiply(result.getSellingPrice()).setScale(2, RoundingMode.HALF_UP));
        }

        inventoryManager.generateStockStatusVOMap(siteId, facilityId, targetSoItem.getAllocatedProductId(), targetSoItem.getBoQty().negate(), PJConstants.SpStockStatus.BO_QTY.getCodeDbid(), stockStatusVOChangeMap);
        inventoryManager.generateStockStatusVOMap(siteId, facilityId, supersedingParts.getPartsId(), targetSoItem.getBoQty(), PJConstants.SpStockStatus.BO_QTY.getCodeDbid(), stockStatusVOChangeMap);

        targetSoItem.setOrderQty(targetSoItem.getOrderQty().subtract(targetQty));
        targetSoItem.setActualQty(targetSoItem.getActualQty().subtract(targetQty));
        targetSoItem.setActualAmt(targetSoItem.getActualQty().multiply(targetSoItem.getSellingPrice()).setScale(2, RoundingMode.HALF_UP));
        targetSoItem.setBoQty(CommonConstants.BIGDECIMAL_ZERO);
        targetSoItem.setWaitingAllocateQty(CommonConstants.BIGDECIMAL_ZERO);

        this.addSalesOrderItem2Map(salesOrderItemForUpdateMap, targetSoItem, keyContainSalesOrderIdFlag);

        return result;
    }

}
