package com.a1stream.common.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.bo.SalesReturnBO;
import com.a1stream.common.bo.SalesReturnDetailBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.InvoiceType;
import com.a1stream.common.constants.PJConstants.OrgRelationType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.exception.PJCustomException;
import com.a1stream.domain.entity.DeliveryOrderItem;
import com.a1stream.domain.entity.Invoice;
import com.a1stream.domain.entity.InvoiceItem;
import com.a1stream.domain.entity.SalesOrder;
import com.a1stream.domain.entity.SalesOrderItem;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.InvoiceItemRepository;
import com.a1stream.domain.repository.InvoiceRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.InvoiceItemVO;
import com.a1stream.domain.vo.InvoiceVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InvoiceManager {

    @Resource
    private InvoiceRepository invoiceRepo;

    @Resource
    private InvoiceItemRepository invoiceItemRepo;

    @Resource
    private DeliveryOrderItemRepository deliveryOrderItemRepo;

    @Resource
    private SalesOrderItemRepository salesOrderItemRepo;

    @Resource
    private SalesOrderRepository salesOrderRepo;

    @Resource
    PickingInstructionManager pickingInstructionManager;

    @Resource
    DeliveryOrderManager deliveryOrderManager;

    @Resource
    SalesOrderManager salesOrderManager;

    @Resource
    InventoryManager inventoryManager;

    @Resource
    private GenerateNoManager generateNoMgr;

    public List<InvoiceVO> doCreateInvoice(List<DeliveryOrderVO> deliveryOrderList) {

        List<InvoiceVO> invoiceVOList = new ArrayList<>();
        List<InvoiceItemVO> invoiceItemVOList = new ArrayList<>();

        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);
        String sysTime = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M);

        if (deliveryOrderList.isEmpty()) {
            throw new BusinessCodedException("no deliveryOrderList exist");
        }

        // 根据order_to_type 进行分组
        Map<String, List<DeliveryOrderVO>> purchaseOrderItemsMap = deliveryOrderList.stream().collect(Collectors.groupingBy(DeliveryOrderVO::getOrderToType));
        List<DeliveryOrderVO> dealerDoList = purchaseOrderItemsMap.containsKey(OrgRelationType.DEALER.getCodeDbid()) ? purchaseOrderItemsMap.get(OrgRelationType.DEALER.getCodeDbid()) : new ArrayList<>();
        List<DeliveryOrderVO> consumerDoList = purchaseOrderItemsMap.containsKey(OrgRelationType.CONSUMER.getCodeDbid()) ? purchaseOrderItemsMap.get(OrgRelationType.CONSUMER.getCodeDbid()) : new ArrayList<>();

        // get all List<DeliveryOrderItem>
        Set<Long> deliveryOrderIds = deliveryOrderList.stream().map(DeliveryOrderVO::getDeliveryOrderId).collect(Collectors.toSet());
        List<DeliveryOrderItemVO> deliveryOrderItemVOList = BeanMapUtils.mapListTo(deliveryOrderItemRepo.findByDeliveryOrderIdIn(deliveryOrderIds), DeliveryOrderItemVO.class);
        if (deliveryOrderItemVOList.isEmpty()) {
            throw new BusinessCodedException("no deliveryOrderItemVOList exist");
        }
        Set<Long> orderItemIds = deliveryOrderItemVOList.stream().map(DeliveryOrderItemVO::getOrderItemId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> salesOrderNos = deliveryOrderItemVOList.stream().map(DeliveryOrderItemVO::getSalesOrderNo).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> siteIds = deliveryOrderItemVOList.stream().map(DeliveryOrderItemVO::getSiteId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, List<DeliveryOrderItemVO>> deliveryOrderItemMap = deliveryOrderItemVOList.stream().collect(Collectors.groupingBy(DeliveryOrderItemVO::getDeliveryOrderId));

        // SalesOrderItem
        List<SalesOrderItemVO> salesOrderItemVOList = !orderItemIds.isEmpty()? BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderItemIdIn(orderItemIds), SalesOrderItemVO.class) : new ArrayList<>();
        Map<Long, SalesOrderItemVO> salesOrderItemMap = salesOrderItemVOList.stream().collect(Collectors.toMap(SalesOrderItemVO::getSalesOrderItemId, Function.identity()));

        List<SalesOrderVO> salesOrderVOList = BeanMapUtils.mapListTo(salesOrderRepo.findBySiteIdInAndOrderNoIn(siteIds, salesOrderNos), SalesOrderVO.class);
        Map<String, SalesOrderVO> salesOrderMap = salesOrderVOList.stream().collect(Collectors.toMap(v -> v.getSiteId() + "#" + v.getOrderNo(), Function.identity()));

        if (!dealerDoList.isEmpty()) {
            // 根据DeliveryOrder.customer_id,from_facility_id,consignee_addr进行分组，根据分组数量生成Invoice,
            // 一个分组生成一笔Invoice
            Map<String, List<DeliveryOrderVO>> dealerDoGroupMap = dealerDoList.stream()
                    .collect(Collectors.groupingBy(v -> v.getCustomerId() + "#" + v.getFromFacilityId() + "#" + v.getConsigneeAddr()));

            for (String groupKey : dealerDoGroupMap.keySet()) {

                List<DeliveryOrderVO> groupDealerDoList = dealerDoGroupMap.get(groupKey);

                // 根据分组后的DeliveryOrder,获取对应的List<DeliveryOrderItem>
                List<DeliveryOrderItemVO> groupDealerDoItemList = new ArrayList<>();
                for (DeliveryOrderVO record : groupDealerDoList) {
                    groupDealerDoItemList.addAll(deliveryOrderItemMap.get(record.getDeliveryOrderId()));
                }
                // build invoice
                DeliveryOrderVO deliveryOrder = groupDealerDoList.get(0);

                BigDecimal totalAmt = groupDealerDoList.stream().map(item -> item.getTotalAmt() != null ? item.getTotalAmt() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                Long invoiceId = buildInvoiceVO(invoiceVOList, sysDate, sysTime, deliveryOrder, totalAmt);
                buildInvoiceItemVO(invoiceItemVOList, salesOrderItemMap, salesOrderMap, groupDealerDoItemList, deliveryOrder.getSiteId(), invoiceId);
            }
        }
        if (!consumerDoList.isEmpty()) {
            // 零售: 一笔DeliveryOrder生成一笔 invoice
            for (DeliveryOrderVO deliveryOrder : consumerDoList) {

                List<DeliveryOrderItemVO> consumerDoItemList = deliveryOrderItemMap.get(deliveryOrder.getDeliveryOrderId());

                Long invoiceId = buildInvoiceVO(invoiceVOList, sysDate, sysTime, deliveryOrder, deliveryOrder.getTotalAmt());
                buildInvoiceItemVO(invoiceItemVOList, salesOrderItemMap, salesOrderMap, consumerDoItemList, deliveryOrder.getSiteId(), invoiceId);
            }
        }

        invoiceRepo.saveInBatch(BeanMapUtils.mapListTo(invoiceVOList, Invoice.class));
        invoiceItemRepo.saveInBatch(BeanMapUtils.mapListTo(invoiceItemVOList, InvoiceItem.class));

        return invoiceVOList;
    }

    public InvoiceVO doSalesReturn(SalesReturnBO model, String siteId) {

        List<SalesReturnDetailBO> details = model.getDetails();
        if (details.isEmpty()) {
            throw new BusinessCodedException("no SalesReturnDetail exist");
        }

        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);
        String sysTime = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M);

        BigDecimal totalAmt = model.getDetails().stream().map(item -> item.getReturnAmount() != null ? item.getReturnAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        InvoiceVO invoiceVO = buildInvoiceVO_SalesReturn(model, sysDate, sysTime, siteId, totalAmt);
        List<InvoiceItemVO> invoiceItemVOList = buildInvoiceItemVO_SalesReturn(details, siteId, invoiceVO.getInvoiceId());

        invoiceRepo.save(BeanMapUtils.mapTo(invoiceVO, Invoice.class));
        invoiceItemRepo.saveInBatch(BeanMapUtils.mapListTo(invoiceItemVOList, InvoiceItem.class));

        return invoiceVO;
    }

    private void buildInvoiceItemVO(List<InvoiceItemVO> invoiceItemVOList
                                , Map<Long, SalesOrderItemVO> salesOrderItemMap
                                , Map<String, SalesOrderVO> salesOrderMap
                                , List<DeliveryOrderItemVO> groupDealerDoItemList
                                , String siteId
                                , Long invoiceId) {
        int seq = 1;
        for (DeliveryOrderItemVO item : groupDealerDoItemList) {

            InvoiceItemVO invoiceItemVO = new InvoiceItemVO();

            invoiceItemVO.setSiteId(siteId);
            invoiceItemVO.setInvoiceId(invoiceId);
            invoiceItemVO.setSeqNo(seq);
            invoiceItemVO.setProductId(item.getProductId());
            invoiceItemVO.setProductCd(item.getProductCd());
            invoiceItemVO.setProductNm(item.getProductNm());
            invoiceItemVO.setQty(item.getDeliveryQty());
            invoiceItemVO.setSellingPriceNotVat(item.getSellingPriceNotVat());
            invoiceItemVO.setAmt(item.getAmt());
            invoiceItemVO.setCost(item.getProductCost());
            invoiceItemVO.setRelatedSoItemId(item.getOrderItemId());
            invoiceItemVO.setSalesOrderNo(item.getSalesOrderNo());
            invoiceItemVO.setOrderedProductId(item.getProductId());
            invoiceItemVO.setOrderedProductCd(item.getProductCd());
            invoiceItemVO.setOrderedProductNm(item.getProductNm());
            invoiceItemVO.setProductClassification(ProductClsType.PART.getCodeDbid());
            invoiceItemVO.setCustomerOrderNo(item.getPurchaseOrderNo());
            invoiceItemVO.setSellingPrice(item.getSellingPrice());
            invoiceItemVO.setAmtNotVat(item.getAmtNotVat());
            invoiceItemVO.setSalesOrderId(item.getSalesOrderId());
            invoiceItemVO.setRelatedDoItemId(item.getDeliveryOrderItemId());


            if (salesOrderItemMap.containsKey(item.getOrderItemId())) {
                SalesOrderItemVO salesOrderItemVO = salesOrderItemMap.get(item.getOrderItemId());

                invoiceItemVO.setStandardPrice(salesOrderItemVO.getStandardPrice());
                invoiceItemVO.setDiscountOffRate(salesOrderItemVO.getDiscountOffRate());
                invoiceItemVO.setTaxRate(salesOrderItemVO.getTaxRate());
            }

            if (salesOrderMap.containsKey(item.getSiteId() + "#" + item.getSalesOrderNo())) {
                SalesOrderVO salesOrderVO = salesOrderMap.get(item.getSiteId() + "#" + item.getSalesOrderNo());

                invoiceItemVO.setOrderDate(salesOrderVO.getOrderDate());
                invoiceItemVO.setOrderType(salesOrderVO.getOrderType());
                invoiceItemVO.setOrderSourceType(salesOrderVO.getOrderSourceType());
            }

            invoiceItemVOList.add(invoiceItemVO);

            seq++;
        }
    }

    private Long buildInvoiceVO(List<InvoiceVO> invoiceVOList
                                , String sysDate
                                , String sysTime
                                , DeliveryOrderVO deliveryOrder
                                , BigDecimal totalAmt) {

        InvoiceVO invoiceVO = InvoiceVO.create();

        String invoiceNo = generateNoMgr.generateInvoiceNo(deliveryOrder.getSiteId());

        invoiceVO.setSiteId(deliveryOrder.getSiteId());
        invoiceVO.setInvoiceDate(sysDate);
        invoiceVO.setInvoiceDatetime(sysTime);
        invoiceVO.setInvoiceNo(invoiceNo);
        invoiceVO.setOrderToType(deliveryOrder.getOrderToType());
        invoiceVO.setFromFacilityId(deliveryOrder.getFromFacilityId());
        invoiceVO.setToFacilityId(deliveryOrder.getToFacilityId());
        invoiceVO.setFromOrganizationId(deliveryOrder.getFromOrganizationId());
        invoiceVO.setToOrganizationId(deliveryOrder.getToOrganizationId());
        invoiceVO.setConsumerId(deliveryOrder.getCmmConsumerId());
        invoiceVO.setConsumerVipNo(deliveryOrder.getConsumerVipNo());
        invoiceVO.setConsumerNmFirst(deliveryOrder.getConsumerNmFirst());
        invoiceVO.setConsumerNmMiddle(deliveryOrder.getConsumerNmMiddle());
        invoiceVO.setConsumerNmLast(deliveryOrder.getConsumerNmLast());
        invoiceVO.setConsumerNmFull(deliveryOrder.getConsumerNmFull());
        invoiceVO.setEmail(deliveryOrder.getEmail());
        invoiceVO.setMobilePhone(deliveryOrder.getMobilePhone());
        invoiceVO.setInvoiceAmt(totalAmt);
        invoiceVO.setInvoiceActualAmt(totalAmt);
        invoiceVO.setInvoiceType(InvoiceType.SALES_INVOICE.getCodeDbid());
        invoiceVO.setProductClassification(ProductClsType.PART.getCodeDbid());
        invoiceVO.setDeliveryAddress(deliveryOrder.getConsigneeAddr());
        invoiceVO.setCustomerNm(deliveryOrder.getCustomerNm());
        invoiceVO.setCustomerCd(deliveryOrder.getCustomerCd());
        invoiceVO.setSpAmt(totalAmt);
        invoiceVO.setUpdateCount(0);
        invoiceVO.setInvoiceActualAmtNotVat(deliveryOrder.getTotalAmtNotVat());
        invoiceVO.setOrderSourceType(ProductClsType.PART.getCodeDbid());

        invoiceVOList.add(invoiceVO);

        return invoiceVO.getInvoiceId();
    }

    private InvoiceVO buildInvoiceVO_SalesReturn(SalesReturnBO model
                                        , String sysDate
                                        , String sysTime
                                        , String siteId
                                        , BigDecimal totalAmt) {

        InvoiceVO invoiceVO = InvoiceVO.create();

        String invoiceNo = generateNoMgr.generateReturnInvoiceNo(siteId);

        invoiceVO.setSiteId(siteId);
        invoiceVO.setInvoiceDate(sysDate);
        invoiceVO.setInvoiceDatetime(sysTime);
        invoiceVO.setInvoiceNo(invoiceNo);
        invoiceVO.setOrderToType(model.getOrderToType());
        invoiceVO.setFromFacilityId(model.getPointId());
        invoiceVO.setFromOrganizationId(model.getFromOrganizationId());
        invoiceVO.setToOrganizationId(model.getCustomerId());
        invoiceVO.setCustomerCd(model.getCustomerCd());
        invoiceVO.setCustomerNm(model.getCustomerName());
        invoiceVO.setConsumerId(model.getConsumerId());
//        invoiceVO.setDeliveryOrderItemId(model.getDeliveryOrderItemId());
        invoiceVO.setInvoiceAmt(totalAmt);
        invoiceVO.setInvoiceActualAmt(totalAmt);
        invoiceVO.setInvoiceType(InvoiceType.SALES_RETURN_INVOICE.getCodeDbid());
        invoiceVO.setRelatedInvoiceId(model.getInvoiceId());
        invoiceVO.setRelatedInvoiceNo(model.getInvoiceNo());
        invoiceVO.setSalesReturnReasonType(model.getReason());
        invoiceVO.setProductClassification(ProductClsType.PART.getCodeDbid());
        invoiceVO.setSpAmt(totalAmt);
        invoiceVO.setUpdateCount(0);

        return invoiceVO;
    }

    private List<InvoiceItemVO> buildInvoiceItemVO_SalesReturn(List<SalesReturnDetailBO> details, String siteId, Long invoiceId) {

        List<InvoiceItemVO> invoiceItemVOList = new ArrayList<>();

        int seq = 1;
        for (SalesReturnDetailBO item : details) {

            InvoiceItemVO invoiceItemVO = new InvoiceItemVO();

            invoiceItemVO.setSiteId(siteId);
            invoiceItemVO.setInvoiceId(invoiceId);
            invoiceItemVO.setSeqNo(seq);
            invoiceItemVO.setProductId(item.getProductId());
            invoiceItemVO.setProductCd(item.getProductCd());
            invoiceItemVO.setProductNm(item.getProductNm());
            invoiceItemVO.setQty(item.getReturnQty());
            invoiceItemVO.setSellingPriceNotVat(item.getReturnPriceNotVat());
            invoiceItemVO.setSellingPrice(item.getReturnPrice());
            invoiceItemVO.setAmt(item.getReturnAmount());
            invoiceItemVO.setCost(item.getCost());
            invoiceItemVO.setRelatedSoItemId(item.getRelatedSoItemId());
            invoiceItemVO.setSalesOrderNo(item.getSalesOrderNo());
            invoiceItemVO.setOrderDate(item.getOrderDate());
            invoiceItemVO.setCustomerOrderNo(item.getCustomerOrderNo());
            invoiceItemVO.setOrderedProductId(item.getProductId());
            invoiceItemVO.setOrderedProductCd(item.getProductCd());
            invoiceItemVO.setOrderedProductNm(item.getProductNm());
            invoiceItemVO.setLocationId(item.getLocationId());
            invoiceItemVO.setLocationCd(item.getLocationCd());
            invoiceItemVO.setRelatedInvoiceItemId(item.getInvoiceItemId());
            invoiceItemVO.setOrderType(item.getOrderType());
            invoiceItemVO.setOrderSourceType(item.getOrderSourceType());
            invoiceItemVO.setProductClassification(ProductClsType.PART.getCodeDbid());
            invoiceItemVO.setTaxRate(item.getTaxRate());

            invoiceItemVOList.add(invoiceItemVO);

            seq++;
        }

        return invoiceItemVOList;
    }

    public void doShipment(Long salesOrderId, Long personId, String personNm) {

        List<Long> doIdsToReport = new ArrayList<>();

        Long deliveryOrderId = null;
        List<DeliveryOrderItemVO> doItemVos =BeanMapUtils.mapListTo(deliveryOrderItemRepo.findBySalesOrderId(salesOrderId), DeliveryOrderItemVO.class);
        if(!doItemVos.isEmpty()){
            deliveryOrderId = doItemVos.get(0).getDeliveryOrderId();
        }

        doIdsToReport.add(deliveryOrderId);

        pickingInstructionManager.doPickingCompletion(doIdsToReport);
        List<DeliveryOrderVO> deliveryOrderList  = deliveryOrderManager.doShippingCompletion(doIdsToReport);

        salesOrderManager.doShippingCompletion(doItemVos);
        inventoryManager.doShippingCompletion(doItemVos,InventoryTransactionType.SALESTOCKOUT.getCodeDbid(), personId, personNm);

        deliveryOrderManager.doInvoicing(doIdsToReport);
        this.doCreateInvoice(deliveryOrderList);
    }

    /**
     * 根据送货单信息创建发票
     * <p>
     * 此方法详细描述了创建发票的整个流程，包括设置发票基本信息和明细信息
     * 它首先创建一个空的发票对象，然后用送货单信息填充该对象，包括发票编号、日期、金额等
     * 接着，根据送货单项创建发票项，包括产品ID、名称、数量、价格等
     * 最后，保存发票和发票项到数据库，并返回发票对象
     * <p>
     * 异常处理：如果创建过程中发生异常，将记录错误日志并抛出自定义异常
     *
     * @param deliveryOrderVO 送货单数据对象，包含创建发票所需的信息
     * @return 返回创建的发票数据对象
     */
    public InvoiceVO doUnitRetailCreateInvoice(DeliveryOrderVO deliveryOrderVO){

        try {
            InvoiceVO invoiceVO = this.createInvoice(deliveryOrderVO);

            InvoiceItemVO invoiceItemVO = this.createInvoiceItemVO(invoiceVO, deliveryOrderVO);

            invoiceRepo.save(BeanMapUtils.mapTo(invoiceVO, Invoice.class));
            invoiceItemRepo.save(BeanMapUtils.mapTo(invoiceItemVO, InvoiceItem.class));
            return invoiceVO;
        } catch (Exception e){
            log.error("做单位零售,创建发票失败: {1}", e);
            throw new PJCustomException("doUnitRetailCreateInvoice error");
        }
    }

    public InvoiceVO createInvoice(DeliveryOrderVO deliveryOrderVO) {

        InvoiceVO invoiceVO = InvoiceVO.create();
        invoiceVO.setSiteId(deliveryOrderVO.getSiteId());
        invoiceVO.setInvoiceDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
        invoiceVO.setInvoiceDatetime(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_H_M));
        invoiceVO.setInvoiceNo(generateNoMgr.generateInvoiceNo(deliveryOrderVO.getSiteId()));
        invoiceVO.setOrderToType(deliveryOrderVO.getOrderToType());
        invoiceVO.setFromFacilityId(deliveryOrderVO.getFromFacilityId());
        invoiceVO.setToFacilityId(deliveryOrderVO.getToFacilityId());
        invoiceVO.setFromOrganizationId(deliveryOrderVO.getFromOrganizationId());
        invoiceVO.setToOrganizationId(deliveryOrderVO.getToOrganizationId());
        invoiceVO.setConsumerId(deliveryOrderVO.getCmmConsumerId());
        invoiceVO.setConsumerVipNo(deliveryOrderVO.getConsumerVipNo());
        invoiceVO.setConsumerNmFirst(deliveryOrderVO.getConsumerNmFirst());
        invoiceVO.setConsumerNmMiddle(deliveryOrderVO.getConsumerNmMiddle());
        invoiceVO.setConsumerNmLast(deliveryOrderVO.getConsumerNmLast());
        invoiceVO.setConsumerNmFull(deliveryOrderVO.getConsumerNmFull());
        invoiceVO.setEmail(deliveryOrderVO.getEmail());
        invoiceVO.setMobilePhone(deliveryOrderVO.getMobilePhone());
        invoiceVO.setInvoiceAmt(deliveryOrderVO.getTotalAmt());
        invoiceVO.setInvoiceActualAmt(deliveryOrderVO.getTotalAmt());
        invoiceVO.setInvoiceType(InvoiceType.SALES_INVOICE.getCodeDbid());
        invoiceVO.setProductClassification(ProductClsType.GOODS.getCodeDbid());
        invoiceVO.setDeliveryAddress(deliveryOrderVO.getConsigneeAddr());
        invoiceVO.setCustomerNm(deliveryOrderVO.getCustomerNm());

        return invoiceVO;
    }

    private InvoiceItemVO createInvoiceItemVO(InvoiceVO invoiceVO, DeliveryOrderVO deliveryOrderVO) {

        List<DeliveryOrderItem> deliveryOrderItemList = deliveryOrderItemRepo.findByDeliveryOrderId(deliveryOrderVO.getDeliveryOrderId());
        DeliveryOrderItem deliveryOrderItem = deliveryOrderItemList.get(0);
        SalesOrderItem salesOrderItem = salesOrderItemRepo.findBySalesOrderItemId(deliveryOrderItem.getOrderItemId());
        SalesOrder salesOrder = salesOrderRepo.findBySiteIdAndOrderNo(deliveryOrderItem.getSiteId(), deliveryOrderItem.getSalesOrderNo());

        InvoiceItemVO invoiceItemVO = new InvoiceItemVO();
        invoiceItemVO.setSiteId(invoiceVO.getSiteId());
        invoiceItemVO.setInvoiceId(invoiceVO.getInvoiceId());
        invoiceItemVO.setSeqNo(1);
        invoiceItemVO.setProductId(deliveryOrderItem.getProductId());
        invoiceItemVO.setProductCd(deliveryOrderItem.getProductCd());
        invoiceItemVO.setProductNm(deliveryOrderItem.getProductNm());
        invoiceItemVO.setQty(deliveryOrderItem.getDeliveryQty());
        invoiceItemVO.setSellingPriceNotVat(deliveryOrderItem.getSellingPriceNotVat());
        invoiceItemVO.setSellingPrice(deliveryOrderItem.getSellingPrice());
        invoiceItemVO.setAmt(deliveryOrderItem.getAmt());
        invoiceItemVO.setCost(deliveryOrderItem.getProductCost());
        invoiceItemVO.setStandardPrice(deliveryOrderItem.getStandardPrice());
        invoiceItemVO.setDiscountOffRate(salesOrderItem.getDiscountOffRate());
        invoiceItemVO.setRelatedSoItemId(deliveryOrderItem.getOrderItemId());
        invoiceItemVO.setSalesOrderNo(deliveryOrderItem.getSalesOrderNo());
        invoiceItemVO.setOrderDate(salesOrder.getOrderDate());
        invoiceItemVO.setOrderedProductId(deliveryOrderItem.getProductId());
        invoiceItemVO.setOrderedProductCd(deliveryOrderItem.getProductCd());
        invoiceItemVO.setOrderedProductNm(deliveryOrderItem.getProductNm());
        invoiceItemVO.setOrderType(salesOrder.getOrderType());
        invoiceItemVO.setOrderSourceType(salesOrder.getOrderSourceType());
        invoiceItemVO.setProductClassification(ProductClsType.GOODS.getCodeDbid());
        invoiceItemVO.setTaxRate(salesOrderItem.getTaxRate());

        return invoiceItemVO;
    }
}