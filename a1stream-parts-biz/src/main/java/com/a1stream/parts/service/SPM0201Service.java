package com.a1stream.parts.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants.InterfaceStatus;
import com.a1stream.common.constants.PJConstants.OrderCancelReasonTypeSub;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.QueueStatus;
import com.a1stream.common.constants.PJConstants.SalesOrderActionType;
import com.a1stream.common.constants.PJConstants.SalesOrderPriorityType;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.manager.ConsumerManager;
import com.a1stream.common.manager.CostManager;
import com.a1stream.common.manager.DeliveryOrderManager;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.InvoiceManager;
import com.a1stream.common.manager.PartsManager;
import com.a1stream.common.manager.PartsSalesStockAllocationManager;
import com.a1stream.common.manager.PickingInstructionManager;
import com.a1stream.common.manager.PurchaseOrderManager;
import com.a1stream.common.manager.SalesOrderManager;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.common.model.PartsInfoBO;
import com.a1stream.common.service.ConsumerService;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.bo.parts.SPM020103BO;
import com.a1stream.domain.bo.parts.SPM020103PrintBO;
import com.a1stream.domain.bo.parts.SPM020103PrintDetailBO;
import com.a1stream.domain.entity.Battery;
import com.a1stream.domain.entity.CmmBattery;
import com.a1stream.domain.entity.CmmRegistrationDocument;
import com.a1stream.domain.entity.QueueEinvoice;
import com.a1stream.domain.entity.QueueOrderBkList;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.entity.SalesOrderItem;
import com.a1stream.domain.form.parts.SPM020103Form;
import com.a1stream.domain.repository.BatteryRepository;
import com.a1stream.domain.repository.CmmBatteryRepository;
import com.a1stream.domain.repository.CmmRegistrationDocumentRepository;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.InvoiceItemRepository;
import com.a1stream.domain.repository.InvoiceRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstOrganizationRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.PoSoItemRelationRepository;
import com.a1stream.domain.repository.PurchaseOrderItemRepository;
import com.a1stream.domain.repository.QueueEinvoiceRepository;
import com.a1stream.domain.repository.QueueOrderBkListRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmBatteryVO;
import com.a1stream.domain.vo.CmmRegistrationDocumentVO;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.InvoiceItemVO;
import com.a1stream.domain.vo.InvoiceVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstOrganizationVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.PoSoItemRelationVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.QueueEinvoiceVO;
import com.a1stream.domain.vo.QueueOrderBkListVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Service
public class SPM0201Service {

    @Resource
    PartsSalesStockAllocationManager allocationManager;

    @Resource
    SalesOrderRepository salesOrderRepository;

    @Resource
    SalesOrderItemRepository salesOrderItemRepository;

    @Resource
    SalesOrderManager salesOrderManager;

    @Resource
    InventoryManager inventoryManager;

    @Resource
    DeliveryOrderManager deliveryOrderManager;

    @Resource
    CostManager costManager;

    @Resource
    DeliveryOrderItemRepository deliveryOrderItemRepository;

    @Resource
    PickingInstructionManager pickingInstructionManager;

    @Resource
    InvoiceManager invoiceManager;

    @Resource
    GenerateNoManager generateNoManager;

    @Resource
    MstProductRepository mstProductRepository;

    @Resource
    PoSoItemRelationRepository poSoItemRelationRepository;

    @Resource
    PurchaseOrderManager purchaseOrderManager;

    @Resource
    PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Resource
    ConsumerService consumerService;

    @Resource
    MstFacilityRepository mstFacilityRepository;

    @Resource
    SystemParameterRepository systemParameterRepository;

    @Resource
    CmmBatteryRepository cmmBatteryRepository;

    @Resource
    ConsumerManager consumerManager;

    @Resource
    PartsManager partsManager;

    @Resource
    InvoiceRepository invoiceRepository;

    @Resource
    InvoiceItemRepository invoiceItemRepository;

    @Resource
    MstOrganizationRepository mstOrganizationRepository;

    @Resource
    QueueEinvoiceRepository queueEinvoiceRepository;

    @Resource
    QueueOrderBkListRepository queueOrderBkListRepository;

    @Resource
    BatteryRepository batteryRepository;

    @Resource
    CmmRegistrationDocumentRepository cmmRegistrationDocumentRepository;

    public void newSaveOrder(SalesOrderVO salesOrderVO
                            ,List<SalesOrderItemVO> orderItemVoList
                            ,BaseConsumerForm consumerForm) {

        consumerManager.saveOrUpdateConsumer(consumerForm);
        consumerManager.saveOrUpdateConsumerPrivacyPolicyResult(consumerForm);
        salesOrderVO.setCmmConsumerId(consumerForm.getConsumerId());
        salesOrderVO.setOrderNo(generateNoManager.generateNonSerializedItemSalesOrderNo(salesOrderVO.getSiteId()
                                                                                        , salesOrderVO.getFacilityId()));

        salesOrderRepository.save(BeanMapUtils.mapTo(salesOrderVO, SalesOrder.class));
        salesOrderItemRepository.saveInBatch(BeanMapUtils.mapListTo(orderItemVoList, SalesOrderItem.class));

        salesOrderVO = BeanMapUtils.mapTo(salesOrderRepository.findBySalesOrderId(salesOrderVO.getSalesOrderId()),SalesOrderVO.class);
        orderItemVoList = BeanMapUtils.mapListTo(salesOrderItemRepository.findBySalesOrderId(salesOrderVO.getSalesOrderId()), SalesOrderItemVO.class);
        //调用预定逻辑
        allocationManager.executeStockAllocation(salesOrderVO,orderItemVoList);
        this.executeACC(salesOrderVO);
    }

    public void updateSaveOrder(SalesOrderVO salesOrderVO
                               ,List<SPM020103BO> deleteList
                               ,List<SPM020103BO> insertList
                               ,List<SPM020103BO> modifyList
                               ,List<SPM020103BO> allList
                               ,BaseConsumerForm  consumerForm) {

        consumerManager.saveOrUpdateConsumer(consumerForm);
        consumerManager.saveOrUpdateConsumerPrivacyPolicyResult(consumerForm);
        salesOrderVO.setCmmConsumerId(consumerForm.getConsumerId());
        Set<Long> orderItemIds =allList.stream().map(SPM020103BO::getOrderItemId).collect(Collectors.toSet());
        List<SalesOrderItemVO> salesOrderItemVOList =BeanMapUtils.mapListTo(this.salesOrderItemRepository.findBySalesOrderItemIdIn(orderItemIds),SalesOrderItemVO.class);

        Integer seq = 1;
        Map<Long, List<SalesOrderItemVO>> partsNoOreItemMap = new HashMap<>();
        Map<Long, SalesOrderItemVO> itemIdMap = new HashMap<>();
        for (SalesOrderItemVO salesOrderItem : salesOrderItemVOList) {

            seq =seq.compareTo(NumberUtil.toInteger(salesOrderItem.getSeqNo())+1)>0?seq:NumberUtil.toInteger(salesOrderItem.getSeqNo())+1;

            if (partsNoOreItemMap.containsKey(salesOrderItem.getAllocatedProductId())) {
                partsNoOreItemMap.get(salesOrderItem.getAllocatedProductId()).add(salesOrderItem);
            }else{
                List<SalesOrderItemVO> orderItemList = new ArrayList<SalesOrderItemVO>();
                orderItemList.add(salesOrderItem);
                partsNoOreItemMap.put(salesOrderItem.getAllocatedProductId(), orderItemList);
            }
            itemIdMap.put(salesOrderItem.getSalesOrderItemId(), salesOrderItem);
        }

        SalesOrderItemVO orderItem;
        List<SalesOrderItemVO> orderItemVoList = new ArrayList<>();
        int seqNo=0;
        Set<Long> proIdSet = allList.stream().map(SPM020103BO::getPartsId).collect(Collectors.toSet());
        Set<Long> allocProIdSet = allList.stream().map(SPM020103BO::getSupersedingPartsId).collect(Collectors.toSet());
        proIdSet.addAll(allocProIdSet);
        Map<Long, MstProductVO> proIdWtihProductMap = this.getProductList(proIdSet)
                                                                    .stream()
                                                                    .collect(Collectors.toMap(MstProductVO::getProductId, Function.identity()));


        for(SPM020103BO newRow:insertList) {
            if (partsNoOreItemMap.containsKey(newRow.getPartsId())
            && !StringUtils.equals(CommonConstants.CHAR_Y, newRow.getBatteryFlag())) {

              orderItem = partsNoOreItemMap.get(newRow.getPartsId()).get(0);
              orderItem.setSalesOrderId(salesOrderVO.getSalesOrderId());
              orderItem.setSiteId(salesOrderVO.getSiteId());
              orderItem.setProductId(newRow.getPartsId());
              orderItem.setProductCd(proIdWtihProductMap.get(newRow.getPartsId()).getProductCd());
              orderItem.setProductNm(proIdWtihProductMap.get(newRow.getPartsId()).getLocalDescription());
              orderItem.setAllocatedProductId(newRow.getPartsId());
              orderItem.setAllocatedProductCd(proIdWtihProductMap.get(newRow.getPartsId()).getProductCd());
              orderItem.setAllocatedProductNm(proIdWtihProductMap.get(newRow.getPartsId()).getLocalDescription());
              orderItem.setOrderQty(newRow.getOrderQty());
              orderItem.setActualQty(newRow.getOrderQty());
              orderItem.setTaxRate(newRow.getTaxRate());
              orderItem.setWaitingAllocateQty(newRow.getOrderQty());
              orderItem.setStandardPrice(newRow.getStdPrice());
              orderItem.setSeqNo(seqNo++);
              orderItem.setBoCancelFlag(newRow.getBoCancelSign());
              orderItem.setDiscountOffRate(newRow.getDiscount());
              orderItem.setActualAmt(newRow.getOrderAmt());
              orderItem.setDiscountAmt(newRow.getDiscountAmtVAT());
              orderItem.setBatteryFlag(newRow.getBatteryFlag());
              orderItem.setBatteryId(newRow.getBatteryID());
              orderItem.setSellingPriceNotVat(newRow.getSellingPriceNotVat());
              orderItem.setActualAmtNotVat(newRow.getActualAmtNotVat());
              orderItem.setProductClassification(ProductClsType.PART.getCodeDbid());
              orderItem.setOrderPrioritySeq(salesOrderVO.getOrderPriorityType().equals(SalesOrderPriorityType.SOEO.getCodeDbid()) ? CommonConstants.INTEGER_ONE : CommonConstants.INTEGER_FIVE);
            }else{
                orderItem = new SalesOrderItemVO();
                orderItem.setSalesOrderId(salesOrderVO.getSalesOrderId());
                orderItem.setSiteId(salesOrderVO.getSiteId());
                orderItem.setProductId(newRow.getPartsId());
                orderItem.setProductCd(proIdWtihProductMap.get(newRow.getPartsId()).getProductCd());
                orderItem.setProductNm(proIdWtihProductMap.get(newRow.getPartsId()).getLocalDescription());
                orderItem.setAllocatedProductId(newRow.getPartsId());
                orderItem.setAllocatedProductCd(proIdWtihProductMap.get(newRow.getPartsId()).getProductCd());
                orderItem.setAllocatedProductNm(proIdWtihProductMap.get(newRow.getPartsId()).getLocalDescription());
                orderItem.setOrderQty(newRow.getOrderQty());
                orderItem.setTaxRate(newRow.getTaxRate());
                orderItem.setActualQty(newRow.getOrderQty());
                orderItem.setWaitingAllocateQty(newRow.getOrderQty());
                orderItem.setStandardPrice(newRow.getStdPrice());
                orderItem.setSellingPrice(newRow.getSellingPrice());
                orderItem.setBatteryFlag(newRow.getBatteryFlag());
                orderItem.setBatteryId(newRow.getBatteryID());
                orderItem.setSeqNo(seqNo);
                seqNo++;
                orderItem.setBoCancelFlag(newRow.getBoCancelSign());
                orderItem.setDiscountOffRate(newRow.getDiscount());
                orderItem.setActualAmt(newRow.getOrderAmt());
                orderItem.setDiscountAmt(newRow.getDiscountAmtVAT());
                orderItem.setSellingPriceNotVat(newRow.getSellingPriceNotVat());
                orderItem.setActualAmtNotVat(newRow.getActualAmtNotVat());
                orderItem.setProductClassification(ProductClsType.PART.getCodeDbid());
                orderItem.setSpecialPrice(newRow.getSpecialPriceVAT());
                orderItem.setOrderPrioritySeq(salesOrderVO.getOrderPriorityType().equals(SalesOrderPriorityType.SOEO.getCodeDbid()) ? CommonConstants.INTEGER_ONE : CommonConstants.INTEGER_FIVE);
            }
            salesOrderItemRepository.save(BeanMapUtils.mapTo(orderItem, SalesOrderItem.class) );
            orderItemVoList.add(orderItem);
        }
        for(SPM020103BO modifyRow:modifyList) {
            SalesOrderItemVO salesOrderItem = itemIdMap.get(modifyRow.getOrderItemId());
            salesOrderItem.setStandardPrice(modifyRow.getStdPrice());
            salesOrderItem.setSeqNo(seqNo++);
            salesOrderItem.setBoCancelFlag(modifyRow.getBoCancelSign());
            salesOrderItem.setSellingPrice(modifyRow.getSellingPrice());
            salesOrderItem.setDiscountOffRate(modifyRow.getDiscount());
            salesOrderItem.setTaxRate(modifyRow.getTaxRate());
            salesOrderItem.setActualAmt(modifyRow.getOrderAmt());
            salesOrderItem.setDiscountAmt(modifyRow.getDiscountAmtVAT());
            salesOrderItem.setSpecialPrice(modifyRow.getSpecialPriceVAT());
            salesOrderItem.setOrderPrioritySeq(salesOrderVO.getOrderPriorityType().equals(SalesOrderPriorityType.SOEO.getCodeDbid()) ? CommonConstants.INTEGER_ONE : CommonConstants.INTEGER_FIVE);
            salesOrderItemRepository.save(BeanMapUtils.mapTo(salesOrderItem, SalesOrderItem.class) );
            orderItemVoList.add(salesOrderItem);
        }

        salesOrderItemVOList =BeanMapUtils.mapListTo(this.salesOrderItemRepository.findBySalesOrderId(salesOrderVO.getSalesOrderId()),SalesOrderItemVO.class);
        allocationManager.executeStockAllocation(salesOrderVO, salesOrderItemVOList);
        this.executeACC(salesOrderVO);

        if(!deleteList.isEmpty()) {

            Set<Long> deleteItemIdList = deleteList.stream().map(SPM020103BO::getOrderItemId).collect(Collectors.toSet());
            List<SalesOrderItemVO> itemVoList =BeanMapUtils.mapListTo(this.salesOrderItemRepository.findBySalesOrderItemIdIn(deleteItemIdList),SalesOrderItemVO.class);
            for(SalesOrderItemVO orderItemVo:itemVoList) {

                BigDecimal allocateQty = orderItemVo.getAllocatedQty();
                BigDecimal boQty = orderItemVo.getBoQty();
                BigDecimal waitingAllocateQty = orderItemVo.getWaitingAllocateQty();
                orderItemVo.setCancelPicId(consumerForm.getPersonId());
                orderItemVo.setCancelPicNm(consumerForm.getPersonNm());
                salesOrderManager.doCancel(orderItemVo);

                if ((allocateQty.compareTo(BigDecimal.ZERO))==1){

                    inventoryManager.doUpdateProductStockStatusMinusQty(salesOrderVO.getSiteId()
                                                                      , salesOrderVO.getFacilityId()
                                                                      , orderItemVo.getAllocatedProductId()
                                                                      , SpStockStatus.ALLOCATED_QTY.getCodeDbid()
                                                                      , allocateQty);
                    inventoryManager.doUpdateProductStockStatusPlusQty(salesOrderVO.getSiteId()
                                                                    , salesOrderVO.getFacilityId()
                                                                    , orderItemVo.getAllocatedProductId()
                                                                    , SpStockStatus.ONHAND_QTY.getCodeDbid()
                                                                    , allocateQty);


                }
                if ((boQty.compareTo(BigDecimal.ZERO))==1){
                    inventoryManager.doUpdateProductStockStatusMinusQty(salesOrderVO.getSiteId()
                                                                        , salesOrderVO.getFacilityId()
                                                                        , orderItemVo.getAllocatedProductId()
                                                                        , SpStockStatus.BO_QTY.getCodeDbid(), boQty);

                }

                if(CommonConstants.CHAR_N.equals(salesOrderVO.getDemandExceptionFlag()) ) {
                    BigDecimal cancelledQty = allocateQty.add(boQty).add(waitingAllocateQty);
                    salesOrderManager.updateProductOrderResultSummary(salesOrderVO.getOrderDate()
                                                                    , salesOrderVO.getFacilityId()
                                                                    , orderItemVo.getAllocatedProductId()
                                                                    , salesOrderVO.getSiteId()
                                                                    , cancelledQty);

                }
            }
        }
    }

    public Long pickingInstruction(SalesOrderVO salesOrderVO
                                 , BaseConsumerForm consumerForm) {

        consumerManager.saveOrUpdateConsumer(consumerForm);
        consumerManager.saveOrUpdateConsumerPrivacyPolicyResult(consumerForm);
        List<SalesOrderItemVO> salesOrderItemVOList =BeanMapUtils.mapListTo(this.salesOrderItemRepository.findBySalesOrderId(salesOrderVO.getSalesOrderId()),SalesOrderItemVO.class);
        salesOrderManager.doPickingForSalesOrder(salesOrderVO, salesOrderItemVOList);
        return salesOrderVO.getSalesOrderId();
    }

    public Long doShipment(Long orderId, SPM020103Form screenModel,List<CmmBatteryVO> cmmBatteryVOs,List<BatteryVO> batteryVOs,List<CmmRegistrationDocumentVO> cmmRegistrationDocumentVOs) {

        List<SPM020103BO> updateList = screenModel.getTableDataList().getUpdateRecords();
        if (!ObjectUtils.isEmpty(updateList)&&!updateList.isEmpty()) {
            Map<Long, SPM020103BO> map = updateList.stream().collect(Collectors.toMap(SPM020103BO::getOrderItemId, obj -> obj));
            List<SalesOrderItemVO> data = BeanMapUtils.mapListTo(salesOrderItemRepository.findBySalesOrderItemIdIn(map.keySet()),SalesOrderItemVO.class);
            for (SalesOrderItemVO item : data) {
                SPM020103BO bo = map.get(item.getSalesOrderItemId());
                item.setBatteryId(bo.getBatteryID());
            }
            salesOrderItemRepository.saveInBatch(BeanMapUtils.mapListTo(data, SalesOrderItem.class));
        }

        cmmBatteryRepository.saveInBatch(BeanMapUtils.mapListTo(cmmBatteryVOs, CmmBattery.class));
        batteryRepository.saveInBatch(BeanMapUtils.mapListTo(batteryVOs, Battery.class));
        cmmRegistrationDocumentRepository.saveInBatch(BeanMapUtils.mapListTo(cmmRegistrationDocumentVOs, CmmRegistrationDocument.class));

        SalesOrderVO salesOrderVo =BeanMapUtils.mapTo(salesOrderRepository.findBySalesOrderId(orderId), SalesOrderVO.class);
        if (null == salesOrderVo || !screenModel.getUpdateCount().equals(salesOrderVo.getUpdateCount())) {
            throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.salesOrderNumber"), screenModel.getOrderNo(), ComUtil.t("title.fastSalesEntry_03")}));
        }
        invoiceManager.doShipment(orderId, screenModel.getPersonId(), screenModel.getPersonNm());
        List<InvoiceItemVO> invoiceItemVOList = BeanMapUtils.mapListTo(invoiceItemRepository.findBySalesOrderNo(salesOrderVo.getOrderNo()), InvoiceItemVO.class);
        Long invoiceId = invoiceItemVOList.get(0).getInvoiceId();
        InvoiceVO invoiceVO = BeanMapUtils.mapTo(invoiceRepository.findByInvoiceId(invoiceId), InvoiceVO.class);
        if (CommonConstants.CHAR_Y.equals(screenModel.getDoFlag())) {
            QueueEinvoiceVO queueEinvoiceVO = new QueueEinvoiceVO();
            queueEinvoiceVO.setSiteId(screenModel.getSiteId());
            queueEinvoiceVO.setRelatedOrderId(salesOrderVo.getSalesOrderId());
            queueEinvoiceVO.setRelatedOrderNo(salesOrderVo.getOrderNo());
            queueEinvoiceVO.setRelatedInvoiceId(invoiceVO.getRelatedInvoiceId());
            queueEinvoiceVO.setRelatedInvoiceNo(invoiceVO.getRelatedInvoiceNo());
            queueEinvoiceVO.setInterfCode(ProductClsType.PART.getCodeDbid());
            queueEinvoiceVO.setInvoiceDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
            queueEinvoiceVO.setSendTimes(CommonConstants.INTEGER_ZERO);
            queueEinvoiceVO.setStatus(InterfaceStatus.WAITINGSEND.getCodeDbid());
            queueEinvoiceRepository.save(BeanMapUtils.mapTo(queueEinvoiceVO, QueueEinvoice.class));
        }
        QueueOrderBkListVO queueOrderBkListVO = new QueueOrderBkListVO();
        queueOrderBkListVO.setSiteId(screenModel.getSiteId());
        queueOrderBkListVO.setOrderId(salesOrderVo.getSalesOrderId());
        queueOrderBkListVO.setOrderType(salesOrderVo.getOrderSourceType());
        queueOrderBkListVO.setOrderDate(salesOrderVo.getOrderDate());
        queueOrderBkListVO.setSendTimes(CommonConstants.INTEGER_ZERO);
        queueOrderBkListVO.setStatus(QueueStatus.WAITINGSEND.getCodeDbid());
        queueOrderBkListRepository.save(BeanMapUtils.mapTo(queueOrderBkListVO, QueueOrderBkList.class));
        return orderId;

//       if(invoiceId != null){
//
//           Invoice invoice = nonSerializingBusinessEngine.getInvoiceManager().createInvoiceQuery()
//                                                                             .property(Invoice.INVOICE_ID, invoiceId)
//                                                                             .property(Invoice.SITE_ID, salesorder.getSiteId())
//                                                                             .uniqueResult();
//
//           //For #4901 Create queueInvoiceList
//           SystemParameterInfo sys = systemControlManager.createSystemParameterInfoBaseQuery()
//                                                         .property(SystemParameterInfo.SITE_ID, salesorder.getSiteId())
//                                                         .property(SystemParameterInfo.ENTITY_SYSTEMPARAMETERTYPE_INFO + Xm03GeneralConstants.CHAR_DOT  + SystemParameterType.SYSTEM_PARAMETER_TYPE_DBID
//                                                                 , XM03CodeInfoConstants.SystemParameterTypeSub.KEY_INTERFACETOEINVOICEFLAG)
//                                                         .property(SystemParameterInfo.PARAMETER_VALUE, Xm03GeneralConstants.CHAR_ONE)
//                                                         .uniqueResult();
//           if(sys != null) {
//
//               if(sys.getParameterValue().equals(Xm03GeneralConstants.CHAR_ONE)){
//
//                   String serverName = this.getApServerName();
//
//                   if(StringUtils.isBlank(serverName)) {
//                       serverName = "localhost";
//                   }
//
//                   //create QueueEinvoiceInterfaceList
//                   QueueEinvoiceInterfaceList queList = baseInvoiceManager.createQueueEinvoiceInterfaceList()
//                                                                                   .setInterfCode(XM03CodeInfoConstants.InterfaceCodeTypeSub.KEY_EINVOICE_PART_SALES)
//                                                                                   .setRelatedInvoiceId(invoiceId)
//                                                                                   .setRelatedOrderId(orderId)
//                                                                                   .setStatus(XM03CodeInfoConstants.InterfaceStatusTypeSub.KEY_WAITING_SEND)
//                                                                                   .setSendTimes(Xm03GeneralConstants.INTEGER_ZERO)
//                                                                                   .setSiteId(salesorder.getSiteId())
//                                                                                   .setInvoiceDate(invoice.getInvoiceDate())
//                                                                                   .setServerName(serverName)
//                                                                                   .build();
//                   baseInvoiceManager.getQueueEinvoiceInterfaceListDao().save(queList);
//
//               }
//           }
//       }
    }

    public Long doCancel(Long orderId, SPM020103Form screenModel) {

        SalesOrderVO salesOrderVo =BeanMapUtils.mapTo(salesOrderRepository.findBySalesOrderId(orderId), SalesOrderVO.class);
        if (null == salesOrderVo || !screenModel.getUpdateCount().equals(salesOrderVo.getUpdateCount())) {
            throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.salesOrderNumber"), screenModel.getOrderNo(), ComUtil.t("title.fastSalesEntry_03")}));
        }
        List<SalesOrderItemVO> orderItemVOs = BeanMapUtils.mapListTo(salesOrderItemRepository.findBySalesOrderId(orderId),SalesOrderItemVO.class);

        String allocateDueDate = "";
        BigDecimal zero = new BigDecimal(0);
        BigDecimal allocateQty;
        BigDecimal boQty;
        if (salesOrderVo != null) {
            allocateDueDate =salesOrderVo.getAllocateDueDate();
        }
        for (SalesOrderItemVO salesOrderItem: orderItemVOs){

            allocateQty = salesOrderItem.getAllocatedQty();
            boQty = salesOrderItem.getBoQty();

            // 整单cancel时跳过已cancel的部品
            if (!OrderCancelReasonTypeSub.KEY_MANUALCANCEL.equals(salesOrderItem.getCancelReasonType())) {
                salesOrderItem.setCancelPicId(screenModel.getPersonId());
                salesOrderItem.setCancelPicNm(screenModel.getPersonNm());
                salesOrderManager.doCancel(salesOrderItem);
            }

            if ((allocateQty.compareTo(zero))>0){

               inventoryManager.doUpdateProductStockStatusMinusQty(salesOrderVo.getSiteId()
                                                                , salesOrderVo.getFacilityId()
                                                                , salesOrderItem.getAllocatedProductId()
                                                                , SpStockStatus.ALLOCATED_QTY.getCodeDbid()
                                                                , allocateQty);

               inventoryManager.doUpdateProductStockStatusPlusQty(salesOrderVo.getSiteId()
                                                               , salesOrderVo.getFacilityId()
                                                               , salesOrderItem.getAllocatedProductId()
                                                               , SpStockStatus.ONHAND_QTY.getCodeDbid()
                                                               , allocateQty);

            }

            if ((boQty.compareTo(zero))>0){

                inventoryManager.doUpdateProductStockStatusMinusQty(salesOrderVo.getSiteId()
                                                                , salesOrderVo.getFacilityId()
                                                                , salesOrderItem.getAllocatedProductId()
                                                                , SpStockStatus.BO_QTY.getCodeDbid()
                                                                , boQty);
            }

            // update product order result summary
            //Get waitingAllocateQty
            BigDecimal waitingAllocateQty = salesOrderItem.getWaitingAllocateQty();
            BigDecimal cancelledQty = allocateQty.add(boQty).add(waitingAllocateQty);

            if(CommonConstants.CHAR_Y.equals(salesOrderVo.getDemandExceptionFlag())){
                salesOrderManager.updateProductOrderResultSummary(allocateDueDate
                                                                , salesOrderVo.getFacilityId()
                                                                , salesOrderItem.getAllocatedProductId()
                                                                , salesOrderVo.getSiteId()
                                                                , cancelledQty);
            }
        }
        salesOrderManager.doSetOrderStatus(salesOrderVo, SalesOrderActionType.ORDER_CANCEL);//SalesOrderActionType.ORDER_CANCEL
        return orderId;
    }

    public void executeACC(SalesOrderVO salesOrderVO) {

        Map<Long, BigDecimal> allocatePartsIdWithBoCancelQtyMap = new HashMap<>();
        Map<Long, Long> allocatedPartsIdWithOrderItemIdMap = new HashMap<>();
        List<SalesOrderItemVO> orderItemVoList = BeanMapUtils.mapListTo(salesOrderItemRepository.findBySalesOrderId(salesOrderVO.getSalesOrderId()), SalesOrderItemVO.class);
        for (SalesOrderItemVO eachOrderItem : orderItemVoList){
            List<PoSoItemRelationVO> soPoItemRelations  =BeanMapUtils.mapListTo(poSoItemRelationRepository.findBySalesOrderItemId(eachOrderItem.getSalesOrderItemId()),PoSoItemRelationVO.class);
            if(soPoItemRelations.isEmpty()){

                getBoQtyandorderItemId(allocatePartsIdWithBoCancelQtyMap
                                     , allocatedPartsIdWithOrderItemIdMap
                                     , eachOrderItem);
            } else {

                BigDecimal qty = new BigDecimal(0);
                BigDecimal boQty = eachOrderItem.getBoQty();

                for (PoSoItemRelationVO sopoItemRelation:soPoItemRelations) {

                        PurchaseOrderItemVO poItem= BeanMapUtils.mapTo(purchaseOrderItemRepository.findByPurchaseOrderItemId(sopoItemRelation.getPurchaseOrderItemId()), PurchaseOrderItemVO.class);

                        qty = qty.add(poItem.getOnPurchaseQty()).add(poItem.getReceiveQty());
                }

                if (qty.compareTo(boQty)<0) {
                    allocatePartsIdWithBoCancelQtyMap.put(eachOrderItem.getAllocatedProductId(), boQty.subtract(qty));
                    allocatedPartsIdWithOrderItemIdMap.put(eachOrderItem.getAllocatedProductId(), eachOrderItem.getSalesOrderItemId());
                }
            }
        }

        if(allocatePartsIdWithBoCancelQtyMap.size() > 0 && allocatedPartsIdWithOrderItemIdMap.size() > 0){

            purchaseOrderManager.savePurchaseOrder(allocatedPartsIdWithOrderItemIdMap
                                                 , allocatePartsIdWithBoCancelQtyMap
                                                 , salesOrderVO.getEntryFacilityId()
                                                 , salesOrderVO.getFacilityId()
                                                 , null
                                                 , DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER)
                                                 , salesOrderVO.getSiteId());

        }
    }

    private void getBoQtyandorderItemId(Map<Long, BigDecimal> allocatePartsIdWithBoCancelQtyMap,
                                        Map<Long, Long> allocatedPartsIdWithOrderItemIdMap,
                                        SalesOrderItemVO eachOrderItem) {
                //if have any BO for ACC items, call purchase logic to create EO , BO of other category, still waiting auto PO in night batch
        if(eachOrderItem.getBoQty().compareTo(BigDecimal.ZERO)>0) {
            MstProductVO productVo =BeanMapUtils.mapTo(this.mstProductRepository.findByProductId(eachOrderItem.getAllocatedProductId()), MstProductVO.class) ;
            if ("ACC".equals(productVo.getAllPath().split("\\|")[0])){

                allocatePartsIdWithBoCancelQtyMap.put(eachOrderItem.getAllocatedProductId(), eachOrderItem.getBoQty());
                allocatedPartsIdWithOrderItemIdMap.put(eachOrderItem.getAllocatedProductId(), eachOrderItem.getSalesOrderItemId());
            }
        }
    }

    public Map<Long, BigDecimal> getOnHandQtyMap(String siteId,Long pointId,Set<Long> proIdList){

        List<ProductStockStatusVO> stockVoList = this.inventoryManager.findStockStatusList(siteId
                                                                                         , pointId
                                                                                         , proIdList
                                                                                         ,SpStockStatus.ONHAND_QTY.getCodeDbid());
        return stockVoList.stream().collect(Collectors.toMap(ProductStockStatusVO::getProductId, ProductStockStatusVO::getQuantity));
    }

    public SalesOrderVO timelyFindBysalesOrderId(Long salesOrderId) {

        return BeanMapUtils.mapTo(salesOrderRepository.findBySalesOrderId(salesOrderId), SalesOrderVO.class);
    }

    public List<SalesOrderItemVO> timelyFindItemBysalesOrderId(Long salesOrderId) {

        return BeanMapUtils.mapListTo(salesOrderItemRepository.findBySalesOrderId(salesOrderId), SalesOrderItemVO.class);
    }

    public List<MstProductVO> getProductList(Set<Long> proIdList){

        return BeanMapUtils.mapListTo(this.mstProductRepository.findByProductIdIn(proIdList), MstProductVO.class);
    }

    public MstFacilityVO getFacilityVoByFacilityId(Long facilityId) {

        return BeanMapUtils.mapTo(this.mstFacilityRepository.findByFacilityId(facilityId), MstFacilityVO.class);
    }

    public SystemParameterVO getTaxRate() {

        return BeanMapUtils.mapTo(systemParameterRepository.findBySystemParameterTypeId(MstCodeConstants.SystemParameterType.TAXRATE), SystemParameterVO.class)  ;
    }

    public List<CmmBatteryVO> getCmmBatteryVOList(List<Long> batteryIdList) {

        return BeanMapUtils.mapListTo(cmmBatteryRepository.findByBatteryIdIn(batteryIdList), CmmBatteryVO.class);
    }

    public String timelyFindConsumerPrivacyPolicyResultVO(String siteId
                                                       , String lastNm
                                                       , String middleNm
                                                       , String firstNm
                                                       , String mobilePhone) {
        return consumerManager.getConsumerPolicyInfo( siteId,  lastNm,  middleNm,  firstNm,  mobilePhone);
    }

    public SPM020103PrintBO getFastSalesOrderReportData(Long salesOrderId) {
        return salesOrderRepository.getFastSalesOrderReportData(salesOrderId);
    }

    public List<SPM020103PrintDetailBO> getFastSalesOrderReportDetailData(Long salesOrderId) {
        return salesOrderItemRepository.getFastSalesOrderReportList(salesOrderId);
    }

    public List<SPM020103PrintBO> getPartsSalesInvoiceForDOList(List<Long> invoiceIds) {
        return invoiceRepository.getPartsSalesInvoiceForDOList(invoiceIds);
    }

    public List<PartsInfoBO> getPartsInfoList(List<String> partsNoList, PJUserDetails uc) {

        return partsManager.getPartsInfoList(partsNoList, uc.getDealerCode(), uc.getDefaultPointId(), uc.getTaxPeriod());
    }

    public List<DeliveryOrderItemVO> getDeliveryOrderItemVOList(Long salesOrderId) {

        return BeanMapUtils.mapListTo(deliveryOrderItemRepository.findBySalesOrderId(salesOrderId), DeliveryOrderItemVO.class);
    }

    public List<InvoiceItemVO> getInvoiceItemVOList(String salesOrderNo) {

        return BeanMapUtils.mapListTo(invoiceItemRepository.findBySalesOrderNo(salesOrderNo), InvoiceItemVO.class);
    }

    public MstOrganizationVO getMstOrganizationVO(String siteId, String relationType) {

        return BeanMapUtils.mapTo(mstOrganizationRepository.getCustomer(siteId, relationType), MstOrganizationVO.class);
    }

}
