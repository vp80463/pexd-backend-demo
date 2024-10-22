package com.a1stream.service.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.model.ConstantsBO;
import com.a1stream.domain.bo.service.SVM020201BO;
import com.a1stream.domain.bo.service.SVM020202BO;
import com.a1stream.domain.bo.service.SVM0202PrintBO;
import com.a1stream.domain.entity.QueueData;
import com.a1stream.domain.entity.ServicePayment;
import com.a1stream.domain.entity.ServicePaymentEditHistory;
import com.a1stream.domain.form.service.SVM020201Form;
import com.a1stream.domain.form.service.SVM020202Form;
import com.a1stream.domain.repository.QueueDataRepository;
import com.a1stream.domain.repository.ServicePaymentEditHistoryRepository;
import com.a1stream.domain.repository.ServicePaymentRepository;
import com.a1stream.domain.repository.ServiceRequestRepository;
import com.a1stream.domain.vo.QueueDataVO;
import com.a1stream.domain.vo.ServicePaymentEditHistoryVO;
import com.a1stream.domain.vo.ServicePaymentVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@Service
public class SVM0202Service {

    @Resource
    private ServicePaymentRepository servicePaymentRepository;

    @Resource
    private ServicePaymentEditHistoryRepository servicePaymentEditHistoryRepository;

    @Resource
    private ConstantsLogic constantsLogic;

    @Resource
    private QueueDataRepository queueDataRepo;

    @Resource
    private ServiceRequestRepository serviceRequestRepository;

    public List<SVM020201BO> findServicePaymentList(SVM020201Form form, String siteId) {

        return servicePaymentRepository.findServicePaymentList(form, siteId);
    }

    public SVM020201BO getSpDetailHeader(SVM020202Form form) {

        return servicePaymentRepository.getSpDetailHeader(form);
    }

    public List<SVM020202BO> getSpDetailList(SVM020202Form form) {

        return servicePaymentRepository.getSpDetailList(form);
    }

    public Integer getCheckCountByInvoiceNoAndSerialNo(SVM020202Form form, String siteId) {

        return servicePaymentRepository.getCheckCountByInvoiceNoAndSerialNo(form, siteId);
    }

    public Integer getCheckCountByInvoiceNo(SVM020202Form form, String siteId) {

        return servicePaymentRepository.getCheckCountByInvoiceNo(form, siteId);
    }

    public ServicePaymentVO findServicePaymentVO(Long paymentId, String siteId) {

        return BeanMapUtils.mapTo(servicePaymentRepository.findByPaymentIdAndSiteId(paymentId, siteId), ServicePaymentVO.class);
    }

    public ServicePaymentEditHistoryVO findServicePaymentEditHistoryVO(Long paymentId, String siteId, String paymentStatus) {

        return BeanMapUtils.mapTo(servicePaymentEditHistoryRepository.findByPaymentIdAndSiteIdAndPaymentStatus(paymentId, siteId, paymentStatus), ServicePaymentEditHistoryVO.class);
    }

    public void confirm(ServicePaymentVO servicePaymentVO, ServicePaymentEditHistoryVO servicePaymentEditHistoryVO) {

        if (!Objects.isNull(servicePaymentVO)) {servicePaymentRepository.save(BeanMapUtils.mapTo(servicePaymentVO, ServicePayment.class));}
        if (!Objects.isNull(servicePaymentEditHistoryVO)) {servicePaymentEditHistoryRepository.save(BeanMapUtils.mapTo(servicePaymentEditHistoryVO, ServicePaymentEditHistory.class));}
    }

    public void issue(ServicePaymentVO servicePaymentVO, ServicePaymentEditHistoryVO servicePaymentEditHistoryVO, QueueDataVO queueData) {

        this.confirm(servicePaymentVO, servicePaymentEditHistoryVO);
        if (!Objects.isNull(queueData)) {queueDataRepo.save(BeanMapUtils.mapTo(queueData, QueueData.class));}
    }

    public Map<String, String> getCodeMstS028(String codeId) {

        Map<String, Field[]> constantsFieldMap =  PJConstants.ConstantsFieldMap;
        List<ConstantsBO> constants = constantsLogic.getConstantsData(constantsFieldMap.get(codeId));
        Map<String, String> mstCodeS028Map = new HashMap<>();

        for(ConstantsBO item : constants) {
            mstCodeS028Map.put(item.getCodeDbid(), item.getCodeData2());
        }
        return mstCodeS028Map;
    }

    public SVM0202PrintBO getServiceExpensesClaimStatementPrintData(Long paymentId) {
        return servicePaymentRepository.getServiceExpensesClaimStatementPrintData(paymentId);
    }

    public SVM0202PrintBO getServiceExpensesClaimStatementForEVPrintData(Long paymentId) {
        return servicePaymentRepository.getServiceExpensesClaimStatementForEVPrintData(paymentId);
    }

    public SVM0202PrintBO getServiceExpensesCouponStatementPrintData(Long paymentId) {
        return servicePaymentRepository.getServiceExpensesCouponStatementPrintData(paymentId);
    }

    public SVM0202PrintBO getServiceExpensesCouponStatementPrintDetailData(String siteId, String paymentMonth) {
        return serviceRequestRepository.getServiceExpensesCouponStatementPrintDetailData(siteId, paymentMonth);
    }

}
