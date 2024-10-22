package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.service.JobDetailBO;
import com.a1stream.domain.bo.service.SVM0102PrintServiceJobBO;
import com.a1stream.domain.bo.service.SVM0130PrintServiceJobBO;

public interface ServiceOrderJobRepositoryCustom {

    List<JobDetailBO> listServiceJobByOrderId(Long serviceOrderId);

    List<SVM0102PrintServiceJobBO> getServiceJobPrintList(Long serviceOrderId);

    List<SVM0102PrintServiceJobBO> getServicePaymentJobPrintList(Long serviceOrderId);

    List<SVM0102PrintServiceJobBO> getServicePaymentJobForDoPrintList(Long serviceOrderId);

    List<SVM0130PrintServiceJobBO> get0KmJobCardJobList(Long serviceOrderId);

    List<SVM0130PrintServiceJobBO> get0KmServicePaymentJobList(Long serviceOrderId);
}
