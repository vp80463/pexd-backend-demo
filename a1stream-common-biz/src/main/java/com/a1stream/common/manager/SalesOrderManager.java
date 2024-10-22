package com.a1stream.common.manager;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.bo.UnitRetailCreateOrderBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.SalesOrderStatus;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.DropShipType;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.OrderCancelReasonTypeSub;
import com.a1stream.common.constants.PJConstants.OrgRelationType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.SalesOrderActionType;
import com.a1stream.common.constants.PJConstants.SalesOrderPriorityType;
import com.a1stream.common.exception.PJCustomException;
import com.a1stream.common.model.BaseResult;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.entity.CmmConsumer;
import com.a1stream.domain.entity.CmmPerson;
import com.a1stream.domain.entity.DeliveryOrderItem;
import com.a1stream.domain.entity.MstFacility;
import com.a1stream.domain.entity.MstProduct;
import com.a1stream.domain.entity.OrderSerializedItem;
import com.a1stream.domain.entity.ProductOrderResultSummary;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.entity.SalesOrderCancelHistory;
import com.a1stream.domain.entity.SalesOrderItem;
import com.a1stream.domain.repository.CmmConsumerRepository;
import com.a1stream.domain.repository.CmmPersonRepository;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.DeliveryOrderRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.OrderSerializedItemRepository;
import com.a1stream.domain.repository.ProductOrderResultSummaryRepository;
import com.a1stream.domain.repository.SalesOrderCancelHistoryRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductOrderResultSummaryVO;
import com.a1stream.domain.vo.SalesOrderCancelHistoryVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SalesOrderManager {

    @Resource
    private SalesOrderRepository salesOrderRepo;

    @Resource
    private SalesOrderItemRepository salesOrderItemRepo;

    @Resource
    private SalesOrderCancelHistoryRepository salesOrderCancelHistRepo;

    @Resource
    private ProductOrderResultSummaryRepository productOrderResultSummaryRepo;

    @Resource
    private MstProductRepository productRepo;

    @Resource
    CostManager costManager;

    @Resource
    private DeliveryOrderRepository deliveryOrderRepo;

    @Resource
    private DeliveryOrderItemRepository deliveryOrderItemRepo;

    @Resource
    DeliveryOrderManager deliveryOrderManager;

    @Resource
    PickingInstructionManager pickingInstructionManager;

    @Resource
    private GenerateNoManager generateNoManager;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private OrderSerializedItemRepository orderSerializedItemRepository;

    @Resource
    private CmmPersonRepository cmmPersonRepository;

    @Resource
    private CmmConsumerRepository cmmConsumerRepository;

    public SalesOrderItemVO getLastSupersedingOrderItem(SalesOrderItemVO originalOrderItem){

        SalesOrderItemVO result = originalOrderItem;

        List<SalesOrderItemVO> orderItems =  BeanMapUtils.mapListTo(this.salesOrderItemRepo.getLastSupersedingOrderItem(originalOrderItem.getSiteId(), originalOrderItem.getSeqNo(), originalOrderItem.getSalesOrderId()), SalesOrderItemVO.class);

        result = orderItems.isEmpty() ? originalOrderItem : orderItems.get(0);

        return result;
    }

    public void updateForFast(SalesOrderVO salesOrderVO, List<SalesOrderItemVO> salesOrderItemList) {

        List<SalesOrderItemVO> dbSalesOrderItemList = BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderId(salesOrderVO.getSalesOrderId()), SalesOrderItemVO.class);
        Map<Long, SalesOrderItemVO> salesOrderItemMap = dbSalesOrderItemList.stream().collect(Collectors.toMap(SalesOrderItemVO::getSalesOrderItemId, Function.identity()));

        Set<Long> productIds = salesOrderItemList.stream().map(SalesOrderItemVO::getProductId).collect(Collectors.toSet());
        List<MstProductVO> mstProductList = BeanMapUtils.mapListTo(productRepo.findByProductIdIn(productIds), MstProductVO.class);
        Map<Long, MstProductVO> mstProductMap = mstProductList.stream().collect(Collectors.toMap(MstProductVO::getProductId, Function.identity()));

        List<SalesOrderItemVO> updateSalesOrderItems = new ArrayList<>();

        int seq = 0;
        for (SalesOrderItemVO item : salesOrderItemList) {
            if (salesOrderItemMap.containsKey(item.getSalesOrderItemId())) {
                SalesOrderItemVO dbRecord = salesOrderItemMap.get(item.getSalesOrderItemId());

                setSalesOrderItemVO(mstProductMap, seq, item, dbRecord);
                setSaleOrderItemQty(SalesOrderStatus.SP_WAITINGALLOCATE, dbRecord, item.getActualQty(), BigDecimal.ZERO);

                updateSalesOrderItems.add(dbRecord);
                seq++;
            }
        }

        setSalesOrderVO(salesOrderVO, salesOrderItemMap);

        salesOrderRepo.save(BeanMapUtils.mapTo(salesOrderVO, SalesOrder.class));
        salesOrderItemRepo.saveInBatch(BeanMapUtils.mapListTo(updateSalesOrderItems, SalesOrderItem.class));
    }

    public void doCancel(SalesOrderItemVO salesOrderItemVO) {

        BigDecimal ZERO = BigDecimal.ZERO;
        // 更新SetSaleOrderItemQty

        BigDecimal cancelledQty = salesOrderItemVO.getWaitingAllocateQty().add(salesOrderItemVO.getAllocatedQty()).add(salesOrderItemVO.getBoQty());

        salesOrderItemVO.setWaitingAllocateQty(ZERO);
        salesOrderItemVO.setAllocatedQty(ZERO);
        salesOrderItemVO.setBoQty(ZERO);
        salesOrderItemVO.setCancelDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        salesOrderItemVO.setCancelTime(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M));
        salesOrderItemVO.setCancelReasonType(OrderCancelReasonTypeSub.KEY_MANUALCANCEL);

        setSaleOrderItemQty(SalesOrderStatus.SP_CANCELLED, salesOrderItemVO, cancelledQty, BigDecimal.ZERO);
        salesOrderItemRepo.save(BeanMapUtils.mapTo(salesOrderItemVO, SalesOrderItem.class));

        // 更新sales_order状态
        SalesOrderVO salesOrderVO = BeanMapUtils.mapTo(salesOrderRepo.findBySalesOrderId(salesOrderItemVO.getSalesOrderId()), SalesOrderVO.class);
        doSetOrderStatus(salesOrderVO, SalesOrderActionType.ORDER_CANCEL);//SalesOrderActionType.ORDER_CANCEL
        salesOrderVO.setTotalActualQty(salesOrderVO.getTotalActualQty().subtract(salesOrderItemVO.getCancelQty()));
        salesOrderVO.setTotalActualAmt(salesOrderVO.getTotalActualAmt().subtract(salesOrderItemVO.getActualAmt()));
        salesOrderVO.setTotalActualAmtNotVat(salesOrderVO.getTotalActualAmtNotVat().subtract(salesOrderItemVO.getActualAmtNotVat()));
        salesOrderRepo.save(BeanMapUtils.mapTo(salesOrderVO, SalesOrder.class));

        // 新增SaleOrderCancelHistory
        SalesOrderCancelHistoryVO cancelHist = new SalesOrderCancelHistoryVO();

        try {
            BeanUtils.copyProperties(cancelHist, salesOrderVO);
            BeanUtils.copyProperties(cancelHist, salesOrderItemVO);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        salesOrderCancelHistRepo.save(BeanMapUtils.mapTo(cancelHist, SalesOrderCancelHistory.class));
    }

    public void doShippingCompletion(List<DeliveryOrderItemVO> deliveryOrderItems) {

        Set<Long> orderItemIds = deliveryOrderItems.stream().map(DeliveryOrderItemVO::getOrderItemId).collect(Collectors.toSet());
        Set<Long> orderIds = deliveryOrderItems.stream().map(DeliveryOrderItemVO::getSalesOrderId).collect(Collectors.toSet());

        List<SalesOrderItemVO> salesOrderItemList = BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderItemIdIn(orderItemIds), SalesOrderItemVO.class);
        List<SalesOrderVO> salesOrderList = BeanMapUtils.mapListTo(salesOrderRepo.findBySalesOrderIdIn(orderIds), SalesOrderVO.class);

        BigDecimal ZERO = BigDecimal.ZERO;
        for (SalesOrderItemVO item : salesOrderItemList) {
            setSaleOrderItemQty(SalesOrderStatus.SP_SHIPMENTED, item, item.getInstructionQty(), ZERO);
        }
        salesOrderItemRepo.saveInBatch(BeanMapUtils.mapListTo(salesOrderItemList, SalesOrderItem.class));
        for (SalesOrderVO order : salesOrderList) {
            this.doSetOrderStatus(order, SalesOrderActionType.SHIPPING_COMPLETION);
        }
        salesOrderRepo.saveInBatch(BeanMapUtils.mapListTo(salesOrderList, SalesOrder.class));

    }

    public void updateProductOrderResultSummary(String allocateDueDate, Long facilityId, Long productId, String siteId,BigDecimal qty) {

        String targetYear = allocateDueDate.substring(0,4);
        String targetMonth = allocateDueDate.substring(4,6);

        String qtyName   = "Quantity";
        String monthName = "month";

        BigDecimal monthQty = BigDecimal.ZERO;
        String quantity = CommonConstants.CHAR_ZERO;

        ProductOrderResultSummaryVO summaryVO = BeanMapUtils.mapTo(productOrderResultSummaryRepo.findProductOrderResultSummery(siteId, targetYear, productId, facilityId), ProductOrderResultSummaryVO.class);
        try {
            if (summaryVO == null) {
                summaryVO = buildProductOrderResultSummaryVO(facilityId, productId, siteId, targetYear);

                BeanUtils.setProperty(summaryVO, monthName + targetMonth + qtyName, qty.negate());
            } else {
                quantity = BeanUtils.getProperty(summaryVO, monthName +targetMonth+ qtyName);
                monthQty = NumberUtil.toBigDecimal(quantity).subtract(qty);

                BeanUtils.setProperty(summaryVO, monthName +targetMonth+ qtyName, monthQty);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        productOrderResultSummaryRepo.save(BeanMapUtils.mapTo(summaryVO, ProductOrderResultSummary.class));
    }

    public SalesOrderVO doSetOrderStatus(SalesOrderVO saleOrderVO, String actionType) {

        String modifyOrderStatus = StringUtils.EMPTY;
        switch(actionType) {
            case SalesOrderActionType.PRE_ALLOCATE:
                if (canSetStatus(saleOrderVO, SalesOrderStatus.SP_WAITINGALLOCATE)) {
                    modifyOrderStatus = SalesOrderStatus.SP_WAITINGALLOCATE;
                }
                if (canSetStatus(saleOrderVO, SalesOrderStatus.SP_CANCELLED)) {
                    modifyOrderStatus = SalesOrderStatus.SP_CANCELLED;
                }
                break;
            case SalesOrderActionType.STOCK_ALLOCATE:
                if (canSetStatus(saleOrderVO, SalesOrderStatus.SP_WAITINGALLOCATE)) {
                    modifyOrderStatus = SalesOrderStatus.SP_WAITINGALLOCATE;
                    break;
                }
                if (canSetStatus(saleOrderVO, SalesOrderStatus.SP_WAITINGPICKING)) {
                    modifyOrderStatus = SalesOrderStatus.SP_WAITINGPICKING;
                    break;
                }
                if (canSetStatus(saleOrderVO, SalesOrderStatus.SP_CANCELLED)) {
                    modifyOrderStatus = SalesOrderStatus.SP_CANCELLED;
                    break;
                }
                break;
            case SalesOrderActionType.ORDER_CANCEL:
                if (canSetStatus(saleOrderVO, SalesOrderStatus.SP_CANCELLED)) {
                    modifyOrderStatus = SalesOrderStatus.SP_CANCELLED;
                }
                if (canSetStatus(saleOrderVO, SalesOrderStatus.SP_WAITINGALLOCATE)) {
                    modifyOrderStatus = SalesOrderStatus.SP_WAITINGALLOCATE;
                }
                if (canSetStatus(saleOrderVO, SalesOrderStatus.SP_WAITINGPICKING)) {
                    modifyOrderStatus = SalesOrderStatus.SP_WAITINGPICKING;
                }
                break;
            case SalesOrderActionType.PICKING_INSTRUCTION:
                if (canSetStatus(saleOrderVO, SalesOrderStatus.SP_ONPICKING)) {
                    modifyOrderStatus = SalesOrderStatus.SP_ONPICKING;
                }
                if (canSetStatus(saleOrderVO, SalesOrderStatus.SP_WAITINGPICKING)) {
                    modifyOrderStatus = SalesOrderStatus.SP_WAITINGPICKING;
                }
                if (canSetStatus(saleOrderVO, SalesOrderStatus.SP_CANCELLED)) {
                    modifyOrderStatus = SalesOrderStatus.SP_CANCELLED;
                }
                break;
            case SalesOrderActionType.SHIPPING_COMPLETION:
                if (canSetStatus(saleOrderVO, SalesOrderStatus.SP_SHIPMENTED)) {
                    modifyOrderStatus = SalesOrderStatus.SP_SHIPMENTED;
                }
                if (canSetStatus(saleOrderVO, SalesOrderStatus.SP_ONPICKING)) {
                    modifyOrderStatus = SalesOrderStatus.SP_ONPICKING;
                }
                if (canSetStatus(saleOrderVO, SalesOrderStatus.SP_WAITINGPICKING)) {
                    modifyOrderStatus = SalesOrderStatus.SP_WAITINGPICKING;
                }
                break;
        }
        if (!StringUtils.isEmpty(modifyOrderStatus)) {
            saleOrderVO.setOrderStatus(modifyOrderStatus);
        }

        return saleOrderVO;
    }

    public SalesOrderItemVO setSaleOrderItemQty(String progressType, SalesOrderItemVO orderItem, BigDecimal plusQty, BigDecimal minusQty) {

        if (plusQty.compareTo(BigDecimal.ZERO) == 0 && minusQty.compareTo(BigDecimal.ZERO) == 0 ) {
            return orderItem;
        }

        BigDecimal setQtyResult = BigDecimal.ZERO;
        BigDecimal instructionQtyResult = BigDecimal.ZERO;
        BigDecimal actualQtyResult = BigDecimal.ZERO;
        switch (progressType) {
            case SalesOrderStatus.SP_WAITINGALLOCATE:
                setQtyResult = orderItem.getWaitingAllocateQty().add(plusQty).subtract(minusQty);
                break;
            case SalesOrderStatus.SP_WAITINGPICKING:
                setQtyResult = orderItem.getAllocatedQty().add(plusQty).subtract(minusQty);
                break;
            case SalesOrderStatus.SP_ONPICKING:
                setQtyResult = orderItem.getInstructionQty().add(plusQty).subtract(minusQty);
                break;
            case SalesOrderStatus.SP_SHIPMENTED:
                setQtyResult = orderItem.getShipmentQty().add(plusQty).subtract(minusQty);
                instructionQtyResult = orderItem.getInstructionQty().subtract(plusQty).subtract(minusQty);
                break;
            case SalesOrderStatus.SP_CANCELLED:
                setQtyResult = orderItem.getCancelQty().add(plusQty).subtract(minusQty);
                actualQtyResult = orderItem.getActualQty().subtract(plusQty).subtract(minusQty);
                break;
            default:
                setQtyResult = null;
                actualQtyResult = null;
        }
        boolean updateByOthers = setQtyResult != null && setQtyResult.compareTo(BigDecimal.ZERO) < 0;
        if (!updateByOthers && StringUtils.equals(SalesOrderStatus.SP_CANCELLED, progressType)) {
            updateByOthers = actualQtyResult != null && actualQtyResult.compareTo(BigDecimal.ZERO) < 0;
        }
        if (!updateByOthers && StringUtils.equals(SalesOrderStatus.SP_SHIPMENTED, progressType)) {
            updateByOthers = instructionQtyResult != null && instructionQtyResult.compareTo(BigDecimal.ZERO) < 0;
        }
        if (updateByOthers) {
            SalesOrderVO salesOrderVO = BeanMapUtils.mapTo(salesOrderRepo.findBySalesOrderId(orderItem.getSalesOrderId()), SalesOrderVO.class);
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00314", new String[]{ CodedMessageUtils.getMessage("label.orderNo")
                                                                                                , salesOrderVO.getOrderNo()
                                                                                                , CodedMessageUtils.getMessage("label.salesOrder") }));
        }

        switch (progressType) {
            case SalesOrderStatus.SP_WAITINGALLOCATE:
                orderItem.setWaitingAllocateQty(setQtyResult);
                break;
            case SalesOrderStatus.SP_WAITINGPICKING:
                orderItem.setAllocatedQty(setQtyResult);
                break;
            case SalesOrderStatus.SP_ONPICKING:
                orderItem.setInstructionQty(setQtyResult);
                break;
            case SalesOrderStatus.SP_SHIPMENTED:
                orderItem.setShipmentQty(setQtyResult);
                orderItem.setInstructionQty(instructionQtyResult);
                break;
            case SalesOrderStatus.SP_CANCELLED:
                orderItem.setCancelQty(setQtyResult);
                orderItem.setActualQty(actualQtyResult);
                break;
        }

        return orderItem;
    }

    public boolean canSetStatus(SalesOrderVO salesOrderVO, String orderStatusId) {

        boolean result = false;

        List<SalesOrderItemVO> dbSalesOrderItemList = BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderId(salesOrderVO.getSalesOrderId()), SalesOrderItemVO.class);
        switch(orderStatusId) {
            case SalesOrderStatus.SP_WAITINGALLOCATE:
//                BigDecimal sumWaitingAllocateQty = dbSalesOrderItemList.stream().map(item -> item.getWaitingAllocateQty() != null ? item.getWaitingAllocateQty() : BigDecimal.ZERO)
//                                                                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal sumBoQty = dbSalesOrderItemList.stream().map(item -> item.getBoQty() != null ? item.getBoQty() : BigDecimal.ZERO)
                                                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                result = sumBoQty.compareTo(BigDecimal.ZERO) > 0;
                break;
            case SalesOrderStatus.SP_WAITINGPICKING:
                BigDecimal sumAllocateQty = dbSalesOrderItemList.stream().map(item -> item.getAllocatedQty() != null ? item.getAllocatedQty() : BigDecimal.ZERO)
                                                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal sumBoQty1 = dbSalesOrderItemList.stream().map(item -> item.getBoQty() != null ? item.getBoQty() : BigDecimal.ZERO)
                                                                                  .reduce(BigDecimal.ZERO, BigDecimal::add);

                result = sumAllocateQty.compareTo(BigDecimal.ZERO) > 0&&sumBoQty1.compareTo(BigDecimal.ZERO)==0;
                break;
            case SalesOrderStatus.SP_ONPICKING:
                BigDecimal sumInstructionQty = dbSalesOrderItemList.stream().map(item -> item.getInstructionQty() != null ? item.getInstructionQty() : BigDecimal.ZERO)
                                                                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal sumCancelQty1 = dbSalesOrderItemList.stream().map(item -> item.getCancelQty() != null ? item.getCancelQty() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal sumOrderQty = dbSalesOrderItemList.stream().map(item -> item.getOrderQty() != null ? item.getOrderQty() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                result = sumInstructionQty.add(sumCancelQty1).compareTo(sumOrderQty) == 0;
                break;
            case SalesOrderStatus.SP_SHIPMENTED:
                BigDecimal sumShipmentQty = dbSalesOrderItemList.stream().map(item -> item.getShipmentQty() != null ? item.getShipmentQty() : BigDecimal.ZERO)
                                                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal sumCancelQty2 = dbSalesOrderItemList.stream().map(item -> item.getCancelQty() != null ? item.getCancelQty() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal sumOrderQty2 = dbSalesOrderItemList.stream().map(item -> item.getOrderQty() != null ? item.getOrderQty() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                result = sumShipmentQty.add(sumCancelQty2).compareTo(sumOrderQty2) == 0;
                break;
            case SalesOrderStatus.SP_CANCELLED:
                BigDecimal sumCancelQty = dbSalesOrderItemList.stream().map(item -> item.getCancelQty() != null ? item.getCancelQty() : BigDecimal.ZERO)
                                                                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal sumOrderQty3 = dbSalesOrderItemList.stream().map(item -> item.getOrderQty() != null ? item.getOrderQty() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                result = sumCancelQty.compareTo(sumOrderQty3) == 0;
                break;
        }

        return result;
    }

    private void setSalesOrderVO(SalesOrderVO salesOrderVO, Map<Long, SalesOrderItemVO> salesOrderItemMap) {

        // 更新sales_order的total_actual_qty，total_actual_amt
        BigDecimal totalActualQty = salesOrderItemMap.values().stream().map(item -> item.getActualQty() != null ? item.getActualQty() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalActualAmt = salesOrderItemMap.values().stream().map(item -> item.getActualQty() != null && item.getSellingPriceNotVat() != null
                                                                                ? item.getActualQty().multiply(item.getSellingPriceNotVat())
                                                                                : BigDecimal.ZERO)
                                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        salesOrderVO.setTotalActualQty(totalActualQty);
        salesOrderVO.setTotalActualAmt(totalActualAmt);
        salesOrderVO.setAllocateDueDate(salesOrderVO.getOrderDate());
        salesOrderVO.setOrderStatus(SalesOrderStatus.SP_WAITINGALLOCATE);
        salesOrderVO.setShipCompleteFlag(CommonConstants.CHAR_ONE);
        salesOrderVO.setOrderPriorityType(StringUtils.equals(salesOrderVO.getOrderType(), SalesOrderPriorityType.SOEO.getCodeDbid())? CommonConstants.CHAR_ONE : CommonConstants.CHAR_FIVE);
    }

    private void setSalesOrderItemVO(Map<Long, MstProductVO> mstProductMap, int seq, SalesOrderItemVO item, SalesOrderItemVO dbRecord) {

        dbRecord.setSeqNo(seq);
        dbRecord.setStandardPrice(mstProductMap.containsKey(item.getProductId())? mstProductMap.get(item.getProductId()).getStdRetailPrice() : BigDecimal.ZERO);
        dbRecord.setSpecialPrice(item.getSpecialPrice());
        dbRecord.setDiscountOffRate(item.getDiscountOffRate());
        dbRecord.setSellingPrice(item.getSellingPrice());
        dbRecord.setTaxRate(item.getTaxRate());
        dbRecord.setSellingPriceNotVat(item.getSellingPriceNotVat());
        dbRecord.setActualAmt(item.getActualAmt());
        dbRecord.setDiscountAmt(item.getDiscountAmt());
    }

    private ProductOrderResultSummaryVO buildProductOrderResultSummaryVO(Long facilityId, Long productId, String siteId, String targetYear) {

        ProductOrderResultSummaryVO summaryVO = new ProductOrderResultSummaryVO();

        summaryVO.setSiteId(siteId);
        summaryVO.setFacilityId(facilityId);
        summaryVO.setProductId(productId);
        summaryVO.setTargetYear(targetYear);

        return summaryVO;
    }

    public void canDoPicking(SalesOrderVO salesOrder,List<SalesOrderItemVO> salesOrderItemVOs){
        if (StringUtils.equals(salesOrder.getShipCompleteFlag(), CommonConstants.CHAR_Y)) {
            for(SalesOrderItemVO salesOrderItem : salesOrderItemVOs){
                BigDecimal  boQty =salesOrderItem.getBoQty();
                if (boQty.compareTo(new BigDecimal(0)) == 1) {
                    throw new BusinessCodedException("M.E.10154");//need confirm the error message
                }
            }
        }
    }

    public void doPickingInstruct(SalesOrderVO salesOrderVo,Map<SalesOrderItemVO, BigDecimal> orderItemVoWithPickingQtyMp) {
            for (SalesOrderItemVO member : orderItemVoWithPickingQtyMp.keySet()){
                //Check AllocatedQty
                if (member.getAllocatedQty().compareTo(orderItemVoWithPickingQtyMp.get(member))== -1){
                    //AllocatedQty < PickingRequestQty: Error data have been updated by other users.
                    throw new BusinessCodedException("M.E.10154");//被他人更新，重刷画面
                }
                member.setAllocatedQty(member.getAllocatedQty().subtract(orderItemVoWithPickingQtyMp.get(member)));
                member.setInstructionQty(member.getInstructionQty().add(orderItemVoWithPickingQtyMp.get(member)));
                this.salesOrderItemRepo.save(BeanMapUtils.mapTo(member, SalesOrderItem.class) );
            }
            //TODO Update OrderStatusInfos
            salesOrderVo = this.doSetOrderStatus(salesOrderVo, SalesOrderActionType.PICKING_INSTRUCTION);//SalesOrderActionType.PICKING_INSTRUCTION
            this.salesOrderRepo.saveWithVersionCheck(BeanMapUtils.mapTo(salesOrderVo, SalesOrder.class) );
    }

    public void doPickingForSalesOrder(SalesOrderVO salesOrderVO, List<SalesOrderItemVO> salesOrderItemVOList) {

        this.canDoPicking(salesOrderVO, salesOrderItemVOList);

        DeliveryOrderVO dov = DeliveryOrderVO.create();
        dov.setInventoryTransactionType(InventoryTransactionType.SALESTOCKOUT.getCodeDbid());
        dov.setSiteId(salesOrderVO.getSiteId());
        dov.setFromFacilityId(salesOrderVO.getFacilityId());
        dov.setToFacilityId(salesOrderVO.getFacilityId());
        dov.setToConsumerId(salesOrderVO.getCmmConsumerId());
        dov.setComment(salesOrderVO.getComment());
        dov.setConsigneeAddr(salesOrderVO.getConsigneeAddr());
        dov.setConsumerNmFull(salesOrderVO.getConsumerNmFull());
        dov.setDeliveryOrderDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        dov.setConsumerVipNo(salesOrderVO.getConsumerVipNo());
        dov.setDeliveryStatus(null);
        dov.setProductClassification(ProductClsType.PART.getCodeDbid());
        dov.setOrderToType(salesOrderVO.getOrderToType());
        dov.setOrderSourceType(salesOrderVO.getOrderSourceType());
        dov.setDropShipFlag(DropShipType.NOTDROPSHIP.equals(salesOrderVO.getDropShipType())?CommonConstants.CHAR_N:CommonConstants.CHAR_Y);
        dov.setCmmConsumerId(salesOrderVO.getCmmConsumerId());
        dov.setConsumerNmFirst(salesOrderVO.getConsumerNmFirst());
        dov.setConsumerNmMiddle(salesOrderVO.getConsumerNmMiddle());
        dov.setConsumerNmLast(salesOrderVO.getConsumerNmLast());
        dov.setCustomerCd(salesOrderVO.getCustomerCd());
        dov.setToOrganizationId(salesOrderVO.getCustomerId());
        dov.setCustomerId(salesOrderVO.getCustomerId());
        dov.setCustomerNm(salesOrderVO.getCustomerNm());
        dov.setEmail(salesOrderVO.getEmail());
        dov.setMobilePhone(salesOrderVO.getMobilePhone());
        dov.setTotalAmt(salesOrderVO.getTotalActualAmt());
        dov.setEntryPicId(salesOrderVO.getEntryPicId());
        dov.setEntryPicNm(salesOrderVO.getEntryPicNm());
        dov.setTotalAmtNotVat(salesOrderVO.getTotalActualAmtNotVat());

        DeliveryOrderItemVO doItem;
        Set<Long> proIdList = salesOrderItemVOList.stream().map(SalesOrderItemVO::getAllocatedProductId).collect(Collectors.toSet());
        Map<Long, BigDecimal> proIdWithCostMap = costManager.getProductCostInBulk(salesOrderVO.getSiteId()
                                                                      , CostType.AVERAGE_COST
                                                                      , proIdList);

        List<DeliveryOrderItemVO> doItemVoList = new ArrayList<>();
        BigDecimal allocateQty ;
        int seqNo=0;
        Map<SalesOrderItemVO,BigDecimal> orderItemVoWithPickingQtyMap = new HashMap<>();
        for (SalesOrderItemVO salesOrderItemVO : salesOrderItemVOList) {
           allocateQty = salesOrderItemVO.getAllocatedQty();
          if (allocateQty.compareTo(BigDecimal.ZERO) > 0) {

              orderItemVoWithPickingQtyMap.put(salesOrderItemVO, allocateQty);
              doItem = new DeliveryOrderItemVO();
              doItem.setDeliveryOrderId(dov.getDeliveryOrderId());
              doItem.setSiteId(salesOrderItemVO.getSiteId());
              doItem.setProductId(salesOrderItemVO.getAllocatedProductId());
              doItem.setProductCd(salesOrderItemVO.getAllocatedProductCd());
              doItem.setProductNm(salesOrderItemVO.getAllocatedProductNm());
              doItem.setSellingPrice(salesOrderItemVO.getSellingPrice());
              doItem.setDeliveryQty(allocateQty);
              doItem.setSalesOrderId(salesOrderVO.getSalesOrderId());
              doItem.setSalesOrderNo(salesOrderVO.getOrderNo());
              doItem.setOrderItemId(salesOrderItemVO.getSalesOrderItemId());
              doItem.setProductClassification(ProductClsType.PART.getCodeDbid());
              doItem.setProductCost(proIdWithCostMap.get(salesOrderItemVO.getAllocatedProductId()));
              doItem.setSeqNo(seqNo++);
              doItem.setOriginalDeliveryQty(allocateQty);
              doItem.setAmt(salesOrderItemVO.getActualAmt());
              doItem.setSellingPriceNotVat(salesOrderItemVO.getSellingPriceNotVat());
              doItem.setTaxRate(salesOrderItemVO.getTaxRate());
              doItem.setAmtNotVat(salesOrderItemVO.getActualAmtNotVat());
              doItem.setStandardPrice(salesOrderItemVO.getStandardPrice());
              doItemVoList.add(doItem);
          }
        }
        deliveryOrderItemRepo.saveAll(BeanMapUtils.mapListTo(doItemVoList, DeliveryOrderItem.class) );
        deliveryOrderManager.doRegister(dov, salesOrderVO.getFacilityId());
        List<DeliveryOrderVO> dovList = new ArrayList<>();
        dovList.add(dov);
        pickingInstructionManager.createPickingListInfos(dovList, 0, 0, null, dov.getSiteId());
        this.doPickingInstruct(salesOrderVO, orderItemVoWithPickingQtyMap);
    }

    /**
     * 根据单位零售业务模型创建销售订单
     * <p>
     * 此方法根据提供的单位零售业务模型创建销售订单，包括设置订单基本信息和明细信息，
     * 并将订单及其项保存到数据库中。此外，还处理了与订单相关的序列化产品信息。
     *
     * @param model 单位零售业务模型，包含创建销售订单所需的所有信息
     * @return 返回创建的销售订单数据对象
     * @throws PJCustomException 如果创建过程中发生异常，将记录错误日志并抛出自定义异常
     */
    public SalesOrderVO doUnitRetailCreateOrder(UnitRetailCreateOrderBO model){

        try {
            this.validateModel(model);
            SalesOrderVO salesOrderVO = this.populateSalesOrderVO(model);
            MstProduct mstProduct = productRepo.findByProductId(model.getMode1Id());
            SalesOrderItemVO salesOrderItemVO = this.createSalesOrderItemVO(salesOrderVO, model, mstProduct);
            OrderSerializedItem orderSerializedItem = this.createOrderSerializedItem(salesOrderItemVO, salesOrderVO);

            salesOrderRepo.save(BeanMapUtils.mapTo(salesOrderVO, SalesOrder.class));
            salesOrderItemRepo.save(BeanMapUtils.mapTo(salesOrderItemVO, SalesOrderItem.class));
            orderSerializedItemRepository.save(BeanMapUtils.mapTo(orderSerializedItem, OrderSerializedItem.class));

            return salesOrderVO;
        } catch(Exception e) {
            log.error("创建销售订单失败", e);
            throw new PJCustomException("Failed to create a sales order");
        }
    }

    private void validateModel(UnitRetailCreateOrderBO model) {
        if (model.getPointId() == null ||
                model.getPersonId() == null ||
                model.getOwnerConsumerId() == null ||
                model.getSerializedProductId() == null ||
                model.getMode1Id() == null) {
            log.error("缺少字段，校验不通过");
            throw new PJCustomException(BaseResult.ERROR_MESSAGE);
        }
    }

    private SalesOrderVO populateSalesOrderVO(UnitRetailCreateOrderBO model) {

        MstFacility facility = mstFacilityRepository.findByFacilityId(model.getPointId());
        CmmPerson person = cmmPersonRepository.findByPersonId(model.getPersonId());
        CmmConsumer consumer = cmmConsumerRepository.findByConsumerId(model.getOwnerConsumerId());
        MstProduct product = productRepo.findByProductId(model.getSerializedProductId());

        SalesOrderVO salesOrderVO = SalesOrderVO.create();
        salesOrderVO.setSiteId(model.getSiteId());
        salesOrderVO.setOrderNo(generateNoManager.generateNonSerializedItemSalesOrderNo(model.getSiteId(), model.getPointId()));
        salesOrderVO.setOrderDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
        salesOrderVO.setDeliveryPlanDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
        salesOrderVO.setAllocateDueDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
        salesOrderVO.setOrderStatus(SalesOrderStatus.WAITING_SHIPPING);
        salesOrderVO.setOrderToType(OrgRelationType.CONSUMER.getCodeDbid());
        salesOrderVO.setProductClassification(ProductClsType.GOODS.getCodeDbid());
        salesOrderVO.setOrderSourceType(ProductClsType.GOODS.getCodeDbid());
        salesOrderVO.setEntryFacilityId(model.getCurrentPointUid());
        salesOrderVO.setFacilityId(model.getPointId());
        salesOrderVO.setFacilityAddress(facility.getFacilityAbbr());
        salesOrderVO.setOrderType(OrgRelationType.CONSUMER.getCodeDbid());
        salesOrderVO.setCmmConsumerId(model.getOwnerConsumerId());
        salesOrderVO.setConsumerVipNo(consumer.getVipNo());
        salesOrderVO.setConsumerNmFirst(consumer.getFirstNm());
        salesOrderVO.setConsumerNmMiddle(consumer.getMiddleNm());
        salesOrderVO.setConsumerNmLast(consumer.getLastNm());
        salesOrderVO.setConsumerNmFull(consumer.getConsumerFullNm());
        salesOrderVO.setUserConsumerId(model.getUserConsumerId());
        salesOrderVO.setEmail(model.getEmail());
        salesOrderVO.setMobilePhone(model.getMobilePhone());
        salesOrderVO.setCustomerTaxCode(model.getCusTaxCode());
        salesOrderVO.setAddress(model.getAddress());
        salesOrderVO.setEntryPicId(model.getPersonId());
        salesOrderVO.setEntryPicNm(person.getPersonNm());
        salesOrderVO.setSalesPicId(model.getPersonId());
        salesOrderVO.setSalesPicNm(person.getPersonNm());
        salesOrderVO.setPaymentMethodType(model.getPaymentMethod());
        salesOrderVO.setDiscountOffRate(model.getDiscountOffRate());
        salesOrderVO.setTaxRate(model.getTaxRate());
        salesOrderVO.setTotalQty(BigDecimal.ONE);
        salesOrderVO.setTotalAmt(model.getSellingPrice());
        salesOrderVO.setTotalActualQty(BigDecimal.ONE);
        salesOrderVO.setTotalActualAmt((model.getSellingPrice().subtract(model.getDiscountAmt())).setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
        salesOrderVO.setTotalActualAmtNotVat(((model.getSellingPrice().subtract(model.getDiscountAmt())).multiply(BigDecimal.ONE.subtract(model.getTaxRate()))).setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
        salesOrderVO.setGiftDescription(model.getGiftDescription());
        salesOrderVO.setFacilityMultiAddr(model.getPointAddress());
        salesOrderVO.setSpecialReduceFlag(model.getSpecialPrice() != null ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
        salesOrderVO.setEvOrderFlag((model.getBatteryId1() != null || model.getBatteryId2() != null) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
        salesOrderVO.setModelCd(model.getModelCd());
        salesOrderVO.setModelNm(model.getModelName());
        salesOrderVO.setFrameNo(model.getFrameNumber());
        salesOrderVO.setEngineNo(model.getEngineNumber());
        salesOrderVO.setColorNm(model.getColorName());
        salesOrderVO.setSerializedProductId(model.getSerializedProductId());
        salesOrderVO.setModelType(product.getModelType());
        salesOrderVO.setDisplacement(product.getDisplacement());

        return salesOrderVO;
    }

    private SalesOrderItemVO createSalesOrderItemVO(SalesOrderVO salesOrderVO, UnitRetailCreateOrderBO model, MstProduct mstProduct) {

        SalesOrderItemVO salesOrderItemVO = SalesOrderItemVO.create(salesOrderVO.getSiteId(), salesOrderVO.getSalesOrderId());
        salesOrderItemVO.setProductId(model.getMode1Id());
        salesOrderItemVO.setProductCd(mstProduct.getProductCd());
        salesOrderItemVO.setProductNm(mstProduct.getAllNm());
        salesOrderItemVO.setStandardPrice(model.getStandardPrice().setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
        salesOrderItemVO.setSpecialPrice(model.getSpecialPrice().setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
        salesOrderItemVO.setDiscountOffRate(model.getDiscountOffRate());
        salesOrderItemVO.setSellingPrice(model.getSellingPrice().setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
        salesOrderItemVO.setTaxRate(model.getTaxRate());
        salesOrderItemVO.setSellingPriceNotVat((model.getSellingPrice().multiply(BigDecimal.ONE.subtract(model.getTaxRate()))).setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
        salesOrderItemVO.setActualAmt((model.getSellingPrice().subtract(model.getDiscountAmt())).setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
        salesOrderItemVO.setActualAmtNotVat(((model.getSellingPrice().subtract(model.getDiscountAmt())).multiply(BigDecimal.ONE.subtract(model.getTaxRate()))).setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
        salesOrderItemVO.setDiscountAmt(model.getDiscountAmt().setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
        salesOrderItemVO.setProductClassification(ProductClsType.GOODS.getCodeDbid());
        salesOrderItemVO.setOrderQty(BigDecimal.ONE);
        salesOrderItemVO.setActualQty(BigDecimal.ONE);
        salesOrderItemVO.setAllocatedQty(BigDecimal.ONE);
        salesOrderItemVO.setAllocatedProductId(model.getMode1Id());
        salesOrderItemVO.setAllocatedProductCd(mstProduct.getProductCd());
        salesOrderItemVO.setAllocatedProductNm(mstProduct.getAllNm());
        salesOrderItemVO.setSeqNo(1);
        salesOrderItemVO.setOrderPrioritySeq(5);
        salesOrderItemVO.setBatteryFlag((model.getBatteryId1() != null || model.getBatteryId2() != null) ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);

        return salesOrderItemVO;
    }

    private OrderSerializedItem createOrderSerializedItem(SalesOrderItemVO salesOrderItemVO, SalesOrderVO salesOrderVO) {

        OrderSerializedItem orderSerializedItem = new OrderSerializedItem();
        orderSerializedItem.setSiteId(salesOrderItemVO.getSiteId());
        orderSerializedItem.setSerializedProductId(salesOrderVO.getSerializedProductId());
        orderSerializedItem.setOrderItemId(salesOrderItemVO.getSalesOrderItemId());
        orderSerializedItem.setSalesOrderId(salesOrderVO.getSalesOrderId());

        return orderSerializedItem;
    }

    /**
     * 根据单元零售创建订单BO更新销售订单
     * 此方法主要用于更新销售订单的信息，包括消费者信息、订单金额、支付方式等，
     * 以及订单项的价格和折扣信息
     *
     * @param model 单元零售创建订单BO对象，包含更新订单所需的信息
     * @return 返回更新后的销售订单VO对象
     * @throws PJCustomException 如果创建过程中发生异常，将记录错误日志并抛出自定义异常
     */
    public SalesOrderVO doUnitRetailUpdateOrder(UnitRetailCreateOrderBO model){

        try {
            CmmConsumer consumer = cmmConsumerRepository.findByConsumerId(model.getOwnerConsumerId());

            SalesOrder salesOrder = salesOrderRepo.findBySalesOrderId(model.getSalesOrderId());
            salesOrder.setCmmConsumerId(model.getOwnerConsumerId());
            salesOrder.setConsumerVipNo(consumer.getVipNo());
            salesOrder.setConsumerNmFirst(consumer.getFirstNm());
            salesOrder.setConsumerNmMiddle(consumer.getMiddleNm());
            salesOrder.setConsumerNmLast(consumer.getLastNm());
            salesOrder.setConsumerNmFull(consumer.getConsumerFullNm());
            salesOrder.setUserConsumerId(model.getUserConsumerId());
            salesOrder.setEmail(model.getEmail());
            salesOrder.setMobilePhone(model.getMobilePhone());
            salesOrder.setCustomerTaxCode(model.getCusTaxCode());
            salesOrder.setAddress(model.getAddress());
            salesOrder.setPaymentMethodType(model.getPaymentMethod());
            salesOrder.setSpecialReduceFlag(model.getSpecialPrice() != null ? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
            salesOrder.setTotalAmt(model.getSellingPrice());
            salesOrder.setTotalActualAmt((model.getSellingPrice().subtract(model.getDiscountAmt())).setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
            salesOrder.setTotalActualAmtNotVat(((model.getSellingPrice().subtract(model.getDiscountAmt())).multiply(BigDecimal.ONE.subtract(model.getTaxRate()))).setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));

            List<SalesOrderItem> salesOrderItemVOList = salesOrderItemRepo.findBySalesOrderId(salesOrder.getSalesOrderId());
            SalesOrderItem salesOrderItem = getSalesOrderItem(model, salesOrderItemVOList);

            salesOrderRepo.save(salesOrder);
            salesOrderItemRepo.save(salesOrderItem);
            return BeanMapUtils.mapTo(salesOrder, SalesOrderVO.class);
        } catch (Exception e) {
            log.error("更新销售订单失败");
            throw new PJCustomException("Update sales order failed");
        }
    }

    private static SalesOrderItem getSalesOrderItem(UnitRetailCreateOrderBO model, List<SalesOrderItem> salesOrderItemVOList) {
        SalesOrderItem salesOrderItem = salesOrderItemVOList.get(0);

        salesOrderItem.setStandardPrice(model.getStandardPrice().setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
        salesOrderItem.setSpecialPrice(model.getSpecialPrice().setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
        salesOrderItem.setDiscountOffRate(model.getDiscountOffRate());
        salesOrderItem.setSellingPrice(model.getSellingPrice().setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
        salesOrderItem.setTaxRate(model.getTaxRate());
        salesOrderItem.setSellingPriceNotVat((model.getSellingPrice().multiply(BigDecimal.ONE.subtract(model.getTaxRate()))).setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
        salesOrderItem.setActualAmt((model.getSellingPrice().subtract(model.getDiscountAmt())).setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
        salesOrderItem.setActualAmtNotVat(((model.getSellingPrice().subtract(model.getDiscountAmt())).multiply(BigDecimal.ONE.subtract(model.getTaxRate()))).setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
        salesOrderItem.setDiscountAmt(model.getDiscountAmt().setScale(CommonConstants.PRICE_FRAC_SCALE, RoundingMode.HALF_UP));
        return salesOrderItem;
    }

    /**
     * 完成单位零售发货
     * 本方法用于处理零售订单的发货完成操作，包括更新订单状态为已发货，以及记录发货数量
     *
     * @param salesOrderVO 销售订单视图对象，包含订单的详细信息
     * @param salesOrderItemVO 销售订单项视图对象，包含订单中某个商品的详细信息
     * @param requestQty 发货请求的数量，用于指定本次发货的数量
     * @throws PJCustomException 如果创建过程中发生异常，将记录错误日志并抛出自定义异常
     *
     * 注意：本方法假设传入的参数均有效，且发货数量大于零
     */
    public void doUnitRetailShippingCompletion(SalesOrderVO salesOrderVO, SalesOrderItemVO salesOrderItemVO, BigDecimal requestQty){

        if (salesOrderVO != null && salesOrderItemVO != null && requestQty != null && requestQty.compareTo(BigDecimal.ZERO) > 0){

            salesOrderVO.setShipDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
            salesOrderVO.setOrderStatus(SalesOrderStatus.SHIPPED);

            salesOrderItemVO.setAllocatedQty(requestQty);
            salesOrderItemVO.setShipmentQty(requestQty);

        } else {
            log.error("参数不正确");
            throw  new PJCustomException("The parameter is incorrect");
        }
    }
}