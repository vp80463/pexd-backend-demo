package com.a1stream.service.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.a1stream.common.manager.ConsumerManager;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.common.manager.ServiceOrderManager;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.domain.bo.service.SVM0120PrintBO;
import com.a1stream.domain.bo.service.SVM0120PrintServiceJobBO;
import com.a1stream.domain.bo.service.SVM0120PrintServicePartBO;
import com.a1stream.domain.entity.ConsumerPrivacyPolicyResult;
import com.a1stream.domain.entity.ServiceOrder;
import com.a1stream.domain.entity.ServiceOrderItemOtherBrand;
import com.a1stream.domain.parameter.service.SVM012001Parameter;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.ConsumerPrivacyPolicyResultRepository;
import com.a1stream.domain.repository.InvoiceItemRepository;
import com.a1stream.domain.repository.InvoiceRepository;
import com.a1stream.domain.repository.ServiceOrderItemOtherBrandRepository;
import com.a1stream.domain.repository.ServiceOrderRepository;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.ServiceOrderItemOtherBrandVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

@Service
public class SVM0120Service {

    @Resource
    private ServiceOrderRepository serviceOrderRepo;
    @Resource
    private ServiceOrderItemOtherBrandRepository serviceOrderItemOtherBrandRepo;
    @Resource
    private ConsumerPrivacyPolicyResultRepository consumerPrivacyPolicyResultRepo;
    @Resource
    private CmmSerializedProductRepository cmmSerializedProductRepo;
    @Resource
    private GenerateNoManager generateNoMgr;
    @Resource
    private ConsumerManager consumerMgr;
    @Resource
    private ServiceOrderManager serviceOrderMgr;
    @Resource
    private InvoiceRepository invoiceRepo;
    @Resource
    private InvoiceItemRepository invoiceItemRepo;

    public List<ServiceOrderItemOtherBrandVO> timelySearchServiceDetailByOrderId(Long serviceOrderId) {
        return BeanMapUtils.mapListTo(serviceOrderItemOtherBrandRepo.findByServiceOrderId(serviceOrderId), ServiceOrderItemOtherBrandVO.class);
    }

    public void registerServiceOrder(SVM012001Parameter para) {

        this.generateServiceOrderNo(para);

        this.saveServiceOrderBasicInfo(para);

        serviceOrderMgr.calculateOrderSummaryForOtherBrand(para.getServiceOrder().getServiceOrderId());
    }

    public void updateServiceOrder(SVM012001Parameter para) {

        this.saveServiceOrderBasicInfo(para);

        serviceOrderMgr.calculateOrderSummaryForOtherBrand(para.getServiceOrder().getServiceOrderId());
    }

    public void settleServiceOrder(SVM012001Parameter para) {

        //1.由于可能出现更换consumerId的情况，优先更新consumer，并刷新原order中的ID
        this.saveOrUpdateConsumer(para.getConsumerBaseInfo());
        para.getServiceOrder().setConsumerId(para.getConsumerBaseInfo().getConsumerId());
        //2.更新业务表
        this.saveServiceOrderBasicInfo(para);
        //3.创建Invoice
        serviceOrderMgr.doInvoiceForOtherBrand(para.getServiceOrder());

        serviceOrderMgr.calculateOrderSummaryForOtherBrand(para.getServiceOrder().getServiceOrderId());
    }

    public void cancelServiceOrder(SVM012001Parameter para) {

        this.saveServiceOrderBasicInfo(para);
    }

    public CmmSerializedProductVO findCmmSerializedProductByFrameOrPlate(String frameNo, String plateNo) {
        return BeanMapUtils.mapTo(StringUtils.isNotBlank(frameNo) ? cmmSerializedProductRepo.findFirstByFrameNo(frameNo) : cmmSerializedProductRepo.findFirstByPlateNo(plateNo), CmmSerializedProductVO.class);
    }

    public void saveOrUpdateConsumer(BaseConsumerForm form) {
        consumerMgr.saveOrUpdateConsumer(form);
    }

    public List<ServiceOrderItemOtherBrandVO> findServiceDetailListByIds(List<Long> serviceOrderItemOtherBrandIds) {
        return BeanMapUtils.mapListTo(serviceOrderItemOtherBrandRepo.findAllById(serviceOrderItemOtherBrandIds), ServiceOrderItemOtherBrandVO.class);
    }

    public SVM0120PrintBO getOtherBrandBlankJobCardData(Long serviceOrderId) {
        return serviceOrderRepo.getOtherBrandBlankJobCardData(serviceOrderId);
    }

    public SVM0120PrintBO getOtherBrandJobCardHeaderData(Long serviceOrderId) {
        return serviceOrderRepo.getOtherBrandJobCardHeaderData(serviceOrderId);
    }

    public List<SVM0120PrintServiceJobBO> getOtherBrandJobCardJobList(Long serviceOrderId) {
        return serviceOrderItemOtherBrandRepo.getOtherBrandJobCardJobList(serviceOrderId);
    }

    public List<SVM0120PrintServicePartBO> getOtherBrandJobCardPartList(Long serviceOrderId) {
        return serviceOrderItemOtherBrandRepo.getOtherBrandJobCardPartList(serviceOrderId);
    }

    public SVM0120PrintBO getOtherBrandPaymentHeaderData(Long serviceOrderId) {
        return serviceOrderRepo.getOtherBrandPaymentHeaderData(serviceOrderId);
    }

    public List<SVM0120PrintServiceJobBO> getOtherBrandPaymentJobList(Long serviceOrderId) {
        return serviceOrderItemOtherBrandRepo.getOtherBrandPaymentJobList(serviceOrderId);
    }

    public List<SVM0120PrintServicePartBO> getOtherBrandPaymentPartList(Long serviceOrderId) {
        return serviceOrderItemOtherBrandRepo.getOtherBrandPaymentPartList(serviceOrderId);
    }

    private void saveServiceOrderBasicInfo(SVM012001Parameter para) {

        if (!Objects.isNull(para.getServiceOrder())) {serviceOrderRepo.save(BeanMapUtils.mapTo(para.getServiceOrder(), ServiceOrder.class));}
        if (!Objects.isNull(para.getConsumerPrivacyPolicyResult())) {consumerPrivacyPolicyResultRepo.save(BeanMapUtils.mapTo(para.getConsumerPrivacyPolicyResult(), ConsumerPrivacyPolicyResult.class));}
        if (!para.getServiceDetailForDelete().isEmpty()) {serviceOrderItemOtherBrandRepo.deleteAllByIdInBatch(para.getServiceDetailForDelete());}
        if (!para.getServiceDetailForSave().isEmpty()) {serviceOrderItemOtherBrandRepo.saveInBatch(BeanMapUtils.mapListTo(para.getServiceDetailForSave(), ServiceOrderItemOtherBrand.class));}
    }

    private void generateServiceOrderNo(SVM012001Parameter para) {

        String orderNo = generateNoMgr.generateNonSerializedItemSalesOrderNo(para.getServiceOrder().getSiteId(), para.getServiceOrder().getFacilityId());

        para.getServiceOrder().setOrderNo(orderNo);
    }
}
