package com.a1stream.common.manager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.DropShipType;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.InvoiceType;
import com.a1stream.common.constants.PJConstants.OrgRelationType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.SettleType;
import com.a1stream.common.logic.ConsumerLogic;
import com.a1stream.common.logic.ServiceLogic;
import com.a1stream.common.model.PartsVLBO;
import com.a1stream.common.model.ServiceJobVLBO;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.service.BatteryBO;
import com.a1stream.domain.bo.service.JobDetailBO;
import com.a1stream.domain.bo.service.PartDetailBO;
import com.a1stream.domain.bo.service.SituationBO;
import com.a1stream.domain.entity.DeliveryOrderItem;
import com.a1stream.domain.entity.Invoice;
import com.a1stream.domain.entity.InvoiceItem;
import com.a1stream.domain.entity.ServiceOrder;
import com.a1stream.domain.repository.CmmConsumerRepository;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.InvoiceItemRepository;
import com.a1stream.domain.repository.InvoiceRepository;
import com.a1stream.domain.repository.MstProductRelationRepository;
import com.a1stream.domain.repository.PoSoItemRelationRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.SalesOrderItemRepository;
import com.a1stream.domain.repository.SalesOrderRepository;
import com.a1stream.domain.repository.ServiceOrderBatteryRepository;
import com.a1stream.domain.repository.ServiceOrderFaultRepository;
import com.a1stream.domain.repository.ServiceOrderItemOtherBrandRepository;
import com.a1stream.domain.repository.ServiceOrderJobRepository;
import com.a1stream.domain.repository.ServiceOrderRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.InvoiceItemVO;
import com.a1stream.domain.vo.InvoiceVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.ServiceOrderItemOtherBrandVO;
import com.a1stream.domain.vo.ServiceOrderJobVO;
import com.a1stream.domain.vo.ServiceOrderVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

@Component
public class ServiceOrderManager {

    @Resource
    private SalesOrderRepository salesOrderRepo;
    @Resource
    private SalesOrderItemRepository salesOrderItemRepo;
    @Resource
    private PoSoItemRelationRepository poSoItemRelationRepo;
    @Resource
    private CmmConsumerRepository cmmConsumerRepo;
    @Resource
    private ServiceOrderItemOtherBrandRepository serviceOrderItemOtherBrandRepo;
    @Resource
    private DeliveryOrderItemRepository doItemRepo;
    @Resource
    private ServiceOrderJobRepository serviceOrderJobRepo;
    @Resource
    private InvoiceRepository invoiceRepo;
    @Resource
    private InvoiceItemRepository invoiceItemRepo;
    @Resource
    private SystemParameterRepository systemParameterRepo;
    @Resource
    private MstProductRelationRepository mstProductRelationRepo;
    @Resource
    private ProductInventoryRepository productInventoryRepo;
    @Resource
    private ServiceOrderFaultRepository serviceOrderFaultRepo;
    @Resource
    private ServiceOrderBatteryRepository serviceOrderBatteryRepo;
    @Resource
    private ServiceOrderRepository serviceOrderRepo;
    @Resource
    private CostManager costMgr;
    @Resource
    private InventoryManager inventoryMgr;
    @Resource
    private SalesOrderManager salesOrderMgr;
    @Resource
    private DeliveryOrderManager deliveryOrderMgr;
    @Resource
    private PickingInstructionManager pickingInstMgr;
    @Resource
    private GenerateNoManager generateNoMgr;
    @Resource
    private PurchaseOrderManager purchaseOrderMgr;
    @Resource
    private ConsumerLogic consumerLogic;
    @Resource
    private ServiceLogic serviceLogic;

    public void doPickingAndShipment(SalesOrderVO salesOrder, Long personId, String personNm) {
        //重新获取part明细
        List<SalesOrderItemVO> salesOrderItem = BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderId(salesOrder.getSalesOrderId()), SalesOrderItemVO.class);

        //1.仅part全为allocated状态，才可以结束
        this.validateAllPartAllocated(salesOrderItem);

        //2.当有QTY>0的part时，创建deliveryOrder并出库
        if (salesOrderItem.stream().anyMatch(orderItem -> orderItem.getActualQty().compareTo(BigDecimal.ZERO) > 0)) {

            this.doPickingForServiceOrder(salesOrder, salesOrderItem);

            List<DeliveryOrderItemVO> deliveryOrderItemList = BeanMapUtils.mapListTo(doItemRepo.findBySalesOrderId(salesOrder.getSalesOrderId()), DeliveryOrderItemVO.class);

            if (!deliveryOrderItemList.isEmpty()) {

                pickingInstMgr.doPickingCompletion(Arrays.asList(deliveryOrderItemList.get(0).getDeliveryOrderId()));
                deliveryOrderMgr.doShippingCompletion(Arrays.asList(deliveryOrderItemList.get(0).getDeliveryOrderId()));
                salesOrderMgr.doShippingCompletion(deliveryOrderItemList);
                inventoryMgr.doShippingCompletion(deliveryOrderItemList, InventoryTransactionType.SALESTOCKOUT.getCodeDbid(), personId, personNm);
                deliveryOrderMgr.doInvoicing(Arrays.asList(deliveryOrderItemList.get(0).getDeliveryOrderId()));
            }
        }
    }

    public void doInvoice(ServiceOrderVO serviceOrder, SalesOrderVO salesOrder) {

        InvoiceVO invoice = this.buildInvoiceForService(serviceOrder);
        List<InvoiceItemVO> invoiceItem = this.prepareInvoiceItemForService(serviceOrder.getServiceOrderId(), Objects.isNull(salesOrder) ? null : salesOrder.getSalesOrderId(), serviceOrder.getOrderDate(), invoice);

        invoiceRepo.save(BeanMapUtils.mapTo(invoice, Invoice.class));

        invoiceItemRepo.saveInBatch(BeanMapUtils.mapListTo(invoiceItem, InvoiceItem.class));
    }

    public void doInvoiceForOtherBrand(ServiceOrderVO serviceOrder) {

        InvoiceVO invoice = this.buildInvoiceForService(serviceOrder);
        List<InvoiceItemVO> invoiceItem = this.prepareInvoiceItemForOtherBrandService(serviceOrder.getServiceOrderId(), serviceOrder.getOrderDate(), invoice);

        invoiceRepo.save(BeanMapUtils.mapTo(invoice, Invoice.class));

        invoiceItemRepo.saveInBatch(BeanMapUtils.mapListTo(invoiceItem, InvoiceItem.class));
    }

    public void calculateOrderSummary(Long serviceOrderId, Long salesOrderId) {

        ServiceOrderVO serviceOrder = BeanMapUtils.mapTo(serviceOrderRepo.findById(serviceOrderId), ServiceOrderVO.class);

        List<ServiceOrderJobVO> jobList = BeanMapUtils.mapListTo(serviceOrderJobRepo.findByServiceOrderId(serviceOrderId), ServiceOrderJobVO.class);
        List<SalesOrderItemVO> partList = BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderId(salesOrderId), SalesOrderItemVO.class);

        //Job计算含税的总金额， Part计算不含税的总金额
        serviceOrder.setServiceAmt(this.calculateJobAmt(jobList));
        serviceOrder.setPartsAmt(this.calculatePartAmt(partList));
        serviceOrder.setServiceConsumerAmt(this.calculateJobConsumerAmt(jobList));
        serviceOrder.setPartsConsumerAmt(this.calculatePartConsuemrAmt(partList));

        serviceOrderRepo.save(BeanMapUtils.mapTo(serviceOrder, ServiceOrder.class));
    }

    public void calculateOrderSummaryForOtherBrand(Long serviceOrderId) {

        List<ServiceOrderItemOtherBrandVO> serviceDetailList = BeanMapUtils.mapListTo(serviceOrderItemOtherBrandRepo.findByServiceOrderId(serviceOrderId), ServiceOrderItemOtherBrandVO.class);

        ServiceOrderVO serviceOrder = BeanMapUtils.mapTo(serviceOrderRepo.findById(serviceOrderId), ServiceOrderVO.class);

        //Job计算含税的总金额， Part计算不含税的总金额
        serviceOrder.setServiceAmt(serviceDetailList.stream().filter(item -> StringUtils.equals(item.getProductClassification(), ProductClsType.SERVICE.getCodeDbid()))
                                                    .map(item -> Objects.isNull(item.getSellingPrice()) ? BigDecimal.ZERO : item.getSellingPrice())
                                                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        serviceOrder.setPartsAmt(serviceDetailList.stream().filter(item -> StringUtils.equals(item.getProductClassification(), ProductClsType.PART.getCodeDbid()))
                                                  .map(item -> Objects.isNull(item.getActualAmtNotVat()) ? BigDecimal.ZERO : item.getActualAmtNotVat())
                                                  .reduce(BigDecimal.ZERO, BigDecimal::add));

        serviceOrderRepo.save(BeanMapUtils.mapTo(serviceOrder, ServiceOrder.class));
    }

    private BigDecimal calculateJobAmt(List<ServiceOrderJobVO> jobList) {

        return jobList.stream()
                      .map(item -> Objects.isNull(item.getSellingPrice()) ? BigDecimal.ZERO : item.getSellingPrice())
                      .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateJobConsumerAmt(List<ServiceOrderJobVO> jobList) {

        return jobList.stream()
                .filter(job -> StringUtils.equals(job.getSettleTypeId(), SettleType.CUSTOMER.getCodeDbid()))
                .map(item -> Objects.isNull(item.getSellingPrice()) ? BigDecimal.ZERO : item.getSellingPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculatePartAmt(List<SalesOrderItemVO> partList) {

        return partList.stream().filter(item -> item.getActualQty().compareTo(BigDecimal.ZERO) > 0)
                .map(item -> Objects.isNull(item.getActualAmtNotVat()) ? BigDecimal.ZERO : item.getActualAmtNotVat())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculatePartConsuemrAmt(List<SalesOrderItemVO> partList) {

        return partList.stream().filter(item -> item.getActualQty().compareTo(BigDecimal.ZERO) > 0 && StringUtils.equals(item.getSettleTypeId(), SettleType.CUSTOMER.getCodeDbid()))
                .map(item -> Objects.isNull(item.getActualAmtNotVat()) ? BigDecimal.ZERO : item.getActualAmtNotVat())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void setAdditionalInfoForPartDetail(List<PartDetailBO> partList, String siteId, Long pointId) {

        List<Long> partIdList = partList.stream().map(PartDetailBO::getPartId).toList();

        //获取替代件信息
        Map<String, PartsVLBO> supersedingMap = this.findSupersedingPartsIdList(partIdList)
                                                      .stream()
                                                      .collect(Collectors.toMap(PartsVLBO::getId, partsVLBO -> partsVLBO));

        //获取主库位信息
        Map<String, PartsVLBO> mainLocationMap = this.findMainLocationIdList(partIdList, siteId, pointId)
                                                                .stream()
                                                                .collect(Collectors.toMap(PartsVLBO::getId, partsVLBO -> partsVLBO));

        partList.forEach(part -> {

            PartsVLBO superseding = supersedingMap.get(part.getPartId().toString());

            if (!Objects.isNull(superseding)) {
                part.setSupersedingPartId(Long.parseLong(superseding.getSupersedingPartsId()));
                part.setSupersedingPartCd(superseding.getSupersedingPartsCd());
                part.setSupersedingPartNm(superseding.getSupersedingPartsNm());
                part.setSupersedingPartsCdFmt(PartNoUtil.format(superseding.getSupersedingPartsCd()));
            }

            PartsVLBO location = mainLocationMap.get(part.getPartId().toString());

            if (!Objects.isNull(location)) {
                part.setLocationCd(location.getMainLocationCd());
            }
        });
    }

    /**
     * 为按ModelCode获得的job设值price
     */
    public void setPriceValueForJobFromModelCd(String settleTypeId, String taxPeriod, List<ServiceJobVLBO> serviceJobList) {

        SystemParameterVO systemParameterVo = this.findSysParamaterByTypeId(PJConstants.SettleType.CUSTOMER.getCodeDbid().equals(settleTypeId) ? MstCodeConstants.SystemParameterType.STDWORKINGHOURPRICECUSTOMER : MstCodeConstants.SystemParameterType.STDWORKINGHOURPRICEFACTORY);
        BigDecimal hourPrice = systemParameterVo == null ? BigDecimal.ZERO : new BigDecimal(systemParameterVo.getParameterValue());
        BigDecimal defaultTaxRate = this.getJobTaxRateByParaType(MstCodeConstants.SystemParameterType.TAXRATE);

        // 系统时间 > 系统税率设置时间，使用S074JOBTAXRATE相应的税率计算价格。
        if(LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)).compareTo(taxPeriod) >= 0) {
            BigDecimal jobTaxRate = this.getJobTaxRateByParaType(MstCodeConstants.SystemParameterType.JOBTAXRATE);
            for (ServiceJobVLBO serviceJobVLBO : serviceJobList) {
                serviceJobVLBO.setVatRate(jobTaxRate);
                serviceJobVLBO.setStdRetailPrice(serviceLogic.calculateStdRetailPriceForJob(new BigDecimal(serviceJobVLBO.getManHours()).multiply(hourPrice), jobTaxRate, defaultTaxRate));
            }
        }else {
            for (ServiceJobVLBO serviceJobVLBO : serviceJobList) {
                serviceJobVLBO.setVatRate(defaultTaxRate);
                serviceJobVLBO.setStdRetailPrice(new BigDecimal(serviceJobVLBO.getManHours()).multiply(hourPrice));
            }
        }
    }

    /**
     * 为按modelType获得的job设值price
     */
    public void setPriceValueForJobFromModelType(String taxPeriod, List<ServiceJobVLBO> serviceJobList) {

        BigDecimal defaultTaxRate = this.getJobTaxRateByParaType(MstCodeConstants.SystemParameterType.TAXRATE);

        //系统时间 > 系统税率设置时间，使用S074JOBTAXRATE相应的税率计算价格。
        if(LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)).compareTo(taxPeriod) >= 0) {
            BigDecimal jobTaxRate = this.getJobTaxRateByParaType(MstCodeConstants.SystemParameterType.JOBTAXRATE);
            for (ServiceJobVLBO serviceJobVLBO : serviceJobList) {
                serviceJobVLBO.setVatRate(jobTaxRate);
                serviceJobVLBO.setStdRetailPrice(serviceLogic.calculateStdRetailPriceForJob(serviceJobVLBO.getStdRetailPrice(), jobTaxRate, defaultTaxRate));
            }
        }
        else {
            for (ServiceJobVLBO serviceJobVLBO : serviceJobList) {
                serviceJobVLBO.setVatRate(defaultTaxRate);
            }
        }
    }

    /**
    *
    * 库存处理相关
    */
    public void autoPurchaseBoPart(SalesOrderVO salesOrderVO) {

        Map<Long, BigDecimal> allocatePartsIdWithBoCancelQtyMap = new HashMap<>();
        Map<Long, Long> allocatedPartsIdWithOrderItemIdMap = new HashMap<>();

        List<SalesOrderItemVO> orderItemVoList = BeanMapUtils.mapListTo(salesOrderItemRepo.findBySalesOrderId(salesOrderVO.getSalesOrderId()), SalesOrderItemVO.class);
        Map<Long, BigDecimal> poQtyMap = this.getPoQtyBySalesOrderItem(orderItemVoList.stream().map(SalesOrderItemVO::getSalesOrderItemId).toList());

        for (SalesOrderItemVO eachOrderItem : orderItemVoList){

            if(poQtyMap.containsKey(eachOrderItem.getSalesOrderItemId())){

                BigDecimal qty = poQtyMap.get(eachOrderItem.getSalesOrderItemId());
                BigDecimal boQty = eachOrderItem.getBoQty();

                if (qty.compareTo(boQty)<0) {
                    allocatePartsIdWithBoCancelQtyMap.put(eachOrderItem.getAllocatedProductId(), boQty.subtract(qty));
                    allocatedPartsIdWithOrderItemIdMap.put(eachOrderItem.getAllocatedProductId(), eachOrderItem.getSalesOrderItemId());
                }
            } else {

                this.getBoQtyAndOrderItemId(allocatePartsIdWithBoCancelQtyMap, allocatedPartsIdWithOrderItemIdMap, eachOrderItem);
            }
        }

        if(!allocatePartsIdWithBoCancelQtyMap.isEmpty() && !allocatedPartsIdWithOrderItemIdMap.isEmpty()){

            purchaseOrderMgr.savePurchaseOrder(allocatedPartsIdWithOrderItemIdMap
                                             , allocatePartsIdWithBoCancelQtyMap
                                             , salesOrderVO.getEntryFacilityId()
                                             , salesOrderVO.getFacilityId()
                                             , null
                                             , LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD))
                                             , salesOrderVO.getSiteId());

        }
    }

    /**
     * 公有model查询
     */
    public List<SituationBO> searchServiceFaultByOrderId(Long serviceOrderId){
        return BeanMapUtils.mapListTo(serviceOrderFaultRepo.findByServiceOrderId(serviceOrderId), SituationBO.class);
    }

    public List<JobDetailBO> searchServiceJobByOrderId(Long serviceOrderId){
        return serviceOrderJobRepo.listServiceJobByOrderId(serviceOrderId);
    }

    public List<PartDetailBO> searchServicePartByOrderId(Long serviceOrderId){
        return salesOrderItemRepo.listServicePartByOrderId(serviceOrderId);
    }

    public List<BatteryBO> searchServiceBatteryByOrderId(Long serviceOrderId){
        return serviceOrderBatteryRepo.listServiceBatteryByOrderId(serviceOrderId);
    }

    /**
     * 私有model查询
     */
    private SystemParameterVO findSysParamaterByTypeId(String paramaterType) {
        return BeanMapUtils.mapTo(systemParameterRepo.findBySystemParameterTypeId(paramaterType), SystemParameterVO.class);
    }

    private BigDecimal getJobTaxRateByParaType(String taxRateType) {
        SystemParameterVO taxInfo = this.findSysParamaterByTypeId(taxRateType);
        return taxInfo == null ? BigDecimal.TEN : new BigDecimal(taxInfo.getParameterValue());
    }

    private Map<Long, BigDecimal> getPoQtyBySalesOrderItem(List<Long> salesOrderItemIds){

        if (salesOrderItemIds.isEmpty()) {return new HashMap<>();}

        return poSoItemRelationRepo.getPoQtyBySalesOrderItem(salesOrderItemIds)
                .stream()
                .collect(Collectors.toMap(obj -> (Long)obj.get("salesorderitemid"), obj -> (BigDecimal)obj.get("sumqty")));
    }

    private void getBoQtyAndOrderItemId(Map<Long, BigDecimal> allocatePartsIdWithBoCancelQtyMap, Map<Long, Long> allocatedPartsIdWithOrderItemIdMap, SalesOrderItemVO eachOrderItem) {
        //service will register all BO part in EO purchase order
        if(eachOrderItem.getBoQty().compareTo(BigDecimal.ZERO) > 0) {
            allocatePartsIdWithBoCancelQtyMap.put(eachOrderItem.getAllocatedProductId(), eachOrderItem.getBoQty());
            allocatedPartsIdWithOrderItemIdMap.put(eachOrderItem.getAllocatedProductId(), eachOrderItem.getSalesOrderItemId());
        }
    }

    private List<PartsVLBO> findSupersedingPartsIdList(List<Long> productIds) {
        return mstProductRelationRepo.findSupersedingPartsIdList(productIds);
    }

    private List<PartsVLBO> findMainLocationIdList(List<Long> productIds,String siteId, Long facilityId) {
        return productInventoryRepo.findMainLocationIdList(productIds, siteId, facilityId);
    }

    private InvoiceVO buildInvoiceForService(ServiceOrderVO serviceOrder) {

        InvoiceVO result = InvoiceVO.create();

        String invoiceNo = generateNoMgr.generateInvoiceNo(serviceOrder.getSiteId());

        result.setInvoiceNo(invoiceNo);
        result.setSiteId(serviceOrder.getSiteId());
        result.setInvoiceDate(ComUtil.date2str(LocalDate.now()));
        result.setInvoiceDatetime(ComUtil.date2timeHM(LocalDateTime.now()));
        result.setOrderToType(OrgRelationType.CONSUMER.getCodeDbid());
        result.setFromFacilityId(serviceOrder.getFacilityId());
        result.setToFacilityId(serviceOrder.getFacilityId());

        if (!Objects.isNull(serviceOrder.getConsumerId())) {

            result.setConsumerId(serviceOrder.getConsumerId());
            result.setConsumerVipNo(BeanMapUtils.mapTo(cmmConsumerRepo.findById(serviceOrder.getConsumerId()), CmmConsumerVO.class).getVipNo());
            result.setConsumerNmFirst(serviceOrder.getFirstNm());
            result.setConsumerNmMiddle(serviceOrder.getMiddleNm());
            result.setConsumerNmLast(serviceOrder.getLastNm());
            String fullName = consumerLogic.getConsumerFullNm(serviceOrder.getLastNm(), serviceOrder.getMiddleNm(), serviceOrder.getFirstNm());
            result.setConsumerNmFull(fullName);
            result.setEmail(serviceOrder.getEmail());
            result.setMobilePhone(serviceOrder.getMobilePhone());
        }

        result.setInvoiceType(InvoiceType.SALES_INVOICE.getCodeDbid());
        result.setOrderSourceType(ProductClsType.SERVICE.getCodeDbid());
        result.setCashierCd(serviceOrder.getCashierCd());
        result.setCashierNm(serviceOrder.getCashierNm());
        result.setCmmCashierId(serviceOrder.getCashierId());

        return result;
    }

    private List<InvoiceItemVO> prepareInvoiceItemForService(Long serviceOrderId, Long salesOrderId, String orderDate, InvoiceVO invoice){

        List<InvoiceItemVO> result = new ArrayList<>();

        int seq = 1;
        InvoiceItemVO invoiceItem;
        BigDecimal jobAmount = BigDecimal.ZERO;
        BigDecimal partAmount = BigDecimal.ZERO;
        BigDecimal amtNotVat = BigDecimal.ZERO;

        //根据Job创建invoiceItem
        List<ServiceOrderJobVO> jobList = BeanMapUtils.mapListTo(serviceOrderJobRepo.findByServiceOrderId(serviceOrderId), ServiceOrderJobVO.class);

        for (ServiceOrderJobVO job : jobList) {

            invoiceItem = new InvoiceItemVO();

            invoiceItem.setSiteId(job.getSiteId());
            invoiceItem.setInvoiceId(invoice.getInvoiceId());
            invoiceItem.setSeqNo(seq);
            invoiceItem.setProductId(job.getJobId());
            invoiceItem.setProductCd(job.getJobCd());
            invoiceItem.setProductNm(job.getJobNm());
            invoiceItem.setQty(BigDecimal.ONE);
            invoiceItem.setSellingPriceNotVat(job.getSellingPriceNotVat());
            invoiceItem.setAmt(job.getSellingPrice());
            invoiceItem.setProductClassification(ProductClsType.SERVICE.getCodeDbid());
            invoiceItem.setStandardPrice(job.getStandardPrice());
            invoiceItem.setDiscountOffRate(job.getDiscount());
            invoiceItem.setTaxRate(job.getVatRate());
            invoiceItem.setOrderDate(orderDate);
            invoiceItem.setOrderSourceType(ProductClsType.SERVICE.getCodeDbid());

            jobAmount = jobAmount.add(job.getSellingPrice());
            amtNotVat = amtNotVat.add(job.getSellingPriceNotVat());

            result.add(invoiceItem);
            seq++;
        }

        //根据Part创建InvoiceItem, 由于电池的存在（productId可能重复），需要合并处理
        List<DeliveryOrderItemVO> deliveryOrderItemList = BeanMapUtils.mapListTo(doItemRepo.findBySalesOrderId(salesOrderId), DeliveryOrderItemVO.class);
        String key;
        Map<String, InvoiceItemVO> invoiceItemMap = new HashMap<>();

        for (DeliveryOrderItemVO item : deliveryOrderItemList) {

            key = this.getKeyByDoItem(item);

            if (invoiceItemMap.containsKey(key)) {

                invoiceItem = invoiceItemMap.get(key);

                invoiceItem.setQty(invoiceItem.getQty().add(item.getDeliveryQty()));
                invoiceItem.setAmt(invoiceItem.getAmt().add(item.getAmt()));
            }
            else {

                invoiceItem = new InvoiceItemVO();

                invoiceItem.setSiteId(item.getSiteId());
                invoiceItem.setInvoiceId(invoice.getInvoiceId());
                invoiceItem.setSeqNo(seq);
                invoiceItem.setProductId(item.getProductId());
                invoiceItem.setProductCd(item.getProductCd());
                invoiceItem.setProductNm(item.getProductNm());
                invoiceItem.setQty(item.getDeliveryQty());
                invoiceItem.setSellingPriceNotVat(item.getSellingPriceNotVat());
                invoiceItem.setSellingPrice(item.getSellingPrice());
                invoiceItem.setAmt(item.getAmt());
                invoiceItem.setCost(item.getProductCost());
                invoiceItem.setRelatedSoItemId(item.getOrderItemId());
                invoiceItem.setSalesOrderNo(item.getSalesOrderNo());
                invoiceItem.setOrderedProductId(item.getProductId());
                invoiceItem.setOrderedProductCd(item.getProductCd());
                invoiceItem.setOrderedProductNm(item.getProductNm());
                invoiceItem.setProductClassification(ProductClsType.PART.getCodeDbid());
                invoiceItem.setCustomerOrderNo(item.getPurchaseOrderNo());
                invoiceItem.setStandardPrice(item.getStandardPrice());
                invoiceItem.setTaxRate(item.getTaxRate());
                invoiceItem.setOrderDate(orderDate);
                invoiceItem.setOrderSourceType(ProductClsType.SERVICE.getCodeDbid());
            }

            partAmount = partAmount.add(item.getAmt());
            amtNotVat = amtNotVat.add(item.getAmtNotVat());

            invoiceItemMap.put(key, invoiceItem);
            seq++;
        }

        result = Stream.concat(result.stream(), invoiceItemMap.values().stream()).toList();

        invoice.setSvJobAmt(jobAmount);
        invoice.setSvPartsAmt(partAmount);
        invoice.setInvoiceAmt(jobAmount.add(partAmount));
        invoice.setInvoiceActualAmt(invoice.getInvoiceAmt());
//        invoice.setAmtNotVat(amtNotVat);
        invoice.setInvoiceActualAmtNotVat(amtNotVat);

        return result;
    }

    private List<InvoiceItemVO> prepareInvoiceItemForOtherBrandService(Long serviceOrderId, String orderDate, InvoiceVO invoice){

        List<InvoiceItemVO> result = new ArrayList<>();
        InvoiceItemVO invoiceItem;
        BigDecimal jobAmount = BigDecimal.ZERO;
        BigDecimal partAmount = BigDecimal.ZERO;
        BigDecimal amtNotVat = BigDecimal.ZERO;
        int seq = 1;

        List<ServiceOrderItemOtherBrandVO> serviceDetailList = BeanMapUtils.mapListTo(serviceOrderItemOtherBrandRepo.findByServiceOrderId(serviceOrderId), ServiceOrderItemOtherBrandVO.class);

        for (ServiceOrderItemOtherBrandVO member : serviceDetailList) {

            invoiceItem = new InvoiceItemVO();

            //Job
            if (StringUtils.equals(member.getProductClassification(), ProductClsType.SERVICE.getCodeDbid())) {

                invoiceItem.setSiteId(member.getSiteId());
                invoiceItem.setInvoiceId(invoice.getInvoiceId());
                invoiceItem.setSeqNo(seq);
                invoiceItem.setProductId(member.getItemId());
                invoiceItem.setProductCd(member.getItemCd());
                invoiceItem.setProductNm(member.getItemContent());
                invoiceItem.setQty(BigDecimal.ONE);
//                invoiceItem.setSellingPriceNotVat(member.getSellingPriceNotVat());
                invoiceItem.setAmt(member.getSellingPrice());
                invoiceItem.setProductClassification(ProductClsType.SERVICE.getCodeDbid());
                invoiceItem.setStandardPrice(member.getStandardPrice());
                invoiceItem.setDiscountOffRate(member.getDiscount());
                invoiceItem.setTaxRate(member.getTaxRate());
                invoiceItem.setOrderDate(orderDate);
                invoiceItem.setOrderSourceType(ProductClsType.SERVICE.getCodeDbid());

                jobAmount = jobAmount.add(member.getSellingPrice());
//                amtNotVat = amtNotVat.add(member.getSellingPriceNotVat());
            }
            else {
                //Part
                invoiceItem.setSiteId(member.getSiteId());
                invoiceItem.setInvoiceId(invoice.getInvoiceId());
                invoiceItem.setSeqNo(seq);
                invoiceItem.setProductNm(member.getItemContent());
                invoiceItem.setQty(member.getOrderQty());
//                invoiceItem.setSellingPriceNotVat(member.getSellingPriceNotVat());
                invoiceItem.setSellingPrice(member.getSellingPrice());
//                invoiceItem.setAmt(member.getAmt());
                invoiceItem.setOrderedProductNm(member.getItemContent());
                invoiceItem.setProductClassification(ProductClsType.PART.getCodeDbid());
                invoiceItem.setStandardPrice(member.getStandardPrice());
                invoiceItem.setTaxRate(member.getTaxRate());
                invoiceItem.setOrderDate(orderDate);
                invoiceItem.setOrderSourceType(ProductClsType.SERVICE.getCodeDbid());

//                partAmount = partAmount.add(member.getAmt());
//                amtNotVat = amtNotVat.add(member.getAmtNotVat());
            }

            result.add(invoiceItem);
            seq++;
        }

        invoice.setSvJobAmt(jobAmount);
        invoice.setSvPartsAmt(partAmount);
        invoice.setInvoiceAmt(jobAmount.add(partAmount));
        invoice.setInvoiceActualAmt(invoice.getInvoiceAmt());
        invoice.setInvoiceActualAmtNotVat(amtNotVat);

        return result;
    }

    private String getKeyByDoItem(DeliveryOrderItemVO deliveryOrderItem) {
        final String strSeparator = "|";
        return deliveryOrderItem.getProductId() + strSeparator
              + deliveryOrderItem.getProductCost() + strSeparator
              + deliveryOrderItem.getSellingPrice();
    }

    private void validateAllPartAllocated(List<SalesOrderItemVO> orderItemVoList) {

        if (orderItemVoList.stream().anyMatch(orderItem -> orderItem.getActualQty().compareTo(orderItem.getAllocatedQty()) != 0)) {

            throw new BusinessCodedException(ComUtil.t("M.E.00204", new String[] {ComUtil.t("label.quantity"), ComUtil.t("label.allocatedQuantity")}));
        }
    }

    private void doPickingForServiceOrder(SalesOrderVO salesOrderVO, List<SalesOrderItemVO> salesOrderItemVOList) {

        DeliveryOrderVO dov = DeliveryOrderVO.create();
        dov.setInventoryTransactionType(InventoryTransactionType.SALESTOCKOUT.getCodeDbid());
        dov.setSiteId(salesOrderVO.getSiteId());
        dov.setFromFacilityId(salesOrderVO.getFacilityId());
        dov.setToFacilityId(salesOrderVO.getFacilityId());
        dov.setToConsumerId(salesOrderVO.getCmmConsumerId());
        dov.setComment(salesOrderVO.getComment());
        dov.setConsigneeAddr(salesOrderVO.getConsigneeAddr());
        dov.setConsumerNmFull(salesOrderVO.getConsumerNmFull());
        dov.setDeliveryOrderDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
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
        dov.setEmail(salesOrderVO.getEmail());
        dov.setMobilePhone(salesOrderVO.getMobilePhone());
        dov.setTotalAmt(salesOrderVO.getTotalActualAmt());


        Map<Long, BigDecimal> proIdWithCostMap = costMgr.getProductCostInBulk(salesOrderVO.getSiteId()
                                                                            , CostType.AVERAGE_COST
                                                                            , salesOrderItemVOList.stream().map(SalesOrderItemVO::getAllocatedProductId).collect(Collectors.toSet()));

        List<DeliveryOrderItemVO> doItemVoList = new ArrayList<>();
        DeliveryOrderItemVO doItem;
        int seqNo=0;
        Map<SalesOrderItemVO,BigDecimal> orderItemVoWithPickingQtyMap = new HashMap<>();
        for (SalesOrderItemVO salesOrderItem : salesOrderItemVOList) {

          if (salesOrderItem.getAllocatedQty().compareTo(BigDecimal.ZERO) > 0) {

              orderItemVoWithPickingQtyMap.put(salesOrderItem, salesOrderItem.getAllocatedQty());
              doItem = new DeliveryOrderItemVO();
              doItem.setDeliveryOrderId(dov.getDeliveryOrderId());
              doItem.setSiteId(salesOrderItem.getSiteId());
              doItem.setProductId(salesOrderItem.getAllocatedProductId());
              doItem.setProductCd(salesOrderItem.getAllocatedProductCd());
              doItem.setProductNm(salesOrderItem.getAllocatedProductNm());
              doItem.setStandardPrice(salesOrderItem.getStandardPrice());
              doItem.setSellingPrice(this.isConsumerChargeTargetSettleType(salesOrderItem.getSettleTypeId()) ? salesOrderItem.getSellingPrice() : BigDecimal.ZERO);
              doItem.setDeliveryQty(salesOrderItem.getAllocatedQty());
              doItem.setSalesOrderId(salesOrderVO.getSalesOrderId());
              doItem.setSalesOrderNo(salesOrderVO.getOrderNo());
              doItem.setOrderItemId(salesOrderItem.getSalesOrderItemId());
              doItem.setProductClassification(ProductClsType.PART.getCodeDbid());
              doItem.setProductCost(proIdWithCostMap.get(salesOrderItem.getAllocatedProductId()));
              doItem.setSeqNo(seqNo++);
              doItem.setOriginalDeliveryQty(salesOrderItem.getAllocatedQty());
              doItemVoList.add(doItem);
          }
        }
        doItemRepo.saveAll(BeanMapUtils.mapListTo(doItemVoList, DeliveryOrderItem.class) );
        deliveryOrderMgr.doRegister(dov, salesOrderVO.getSalesOrderId());
        pickingInstMgr.createPickingListInfos(Arrays.asList(dov), 0, 0, null, dov.getSiteId());
        salesOrderMgr.doPickingInstruct(BeanMapUtils.mapTo(salesOrderRepo.findById(salesOrderVO.getSalesOrderId()), SalesOrderVO.class), orderItemVoWithPickingQtyMap);
    }

    private boolean isConsumerChargeTargetSettleType(String itemSettleType){
        return StringUtils.equals(SettleType.CUSTOMER.getCodeDbid(), itemSettleType);
    }
}