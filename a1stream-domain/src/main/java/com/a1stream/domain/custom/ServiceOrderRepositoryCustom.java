package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.data.domain.Page;

import com.a1stream.domain.bo.common.EInvoiceInvoiceBO;
import com.a1stream.domain.bo.common.EInvoiceProductsBO;
import com.a1stream.domain.bo.master.CMM010302BO;
import com.a1stream.domain.bo.service.SVM0102PrintBO;
import com.a1stream.domain.bo.service.SVM0109PrintBO;
import com.a1stream.domain.bo.service.SVM0120PrintBO;
import com.a1stream.domain.bo.service.SVM0130PrintBO;
import com.a1stream.domain.bo.service.SVM0130PrintServicePartBO;
import com.a1stream.domain.bo.service.SVQ010201BO;
import com.a1stream.domain.bo.service.SVQ010201ExportBO;
import com.a1stream.domain.bo.service.SVQ010401BO;
import com.a1stream.domain.form.master.CMM010302Form;
import com.a1stream.domain.form.service.SVQ010201Form;
import com.a1stream.domain.form.service.SVQ010401Form;

public interface ServiceOrderRepositoryCustom {

	Page<SVQ010201BO> pageServiceOrder(SVQ010201Form model, String siteId);

	Page<SVQ010401BO> page0KmServiceOrder(SVQ010401Form model, String siteId);

    List<SVQ010201ExportBO> listServiceOrder(SVQ010201Form model, String siteId);

    Integer getServiceOrderCount(String siteId, String orderStatus, String zeroKmFlag, String serviceCategoryId);

    Integer getZeroKmServiceOrderCount(String siteId, String orderStatus, String zeroKmFlag);

    Integer getClaimBatteryServiceOrderCount(String siteId, String orderStatus, String serviceCategoryId);

    List<CMM010302BO> getServiceDetailList(CMM010302Form form, String siteId);

    SVM0102PrintBO getServiceJobCardHeaderData(Long serviceOrderId);

    SVM0102PrintBO getServiceJobCardForDoHeaderData(Long serviceOrderId);

    SVM0102PrintBO getServicePaymentHederData(Long serviceOrderId);

    SVM0102PrintBO getServicePaymentForDoHederData(Long serviceOrderId);

    SVM0120PrintBO getOtherBrandBlankJobCardData(Long serviceOrderId);

    SVM0120PrintBO getOtherBrandJobCardHeaderData(Long serviceOrderId);

    SVM0120PrintBO getOtherBrandPaymentHeaderData(Long serviceOrderId);

    SVM0130PrintBO get0KmJobCardHeaderData(Long serviceOrderId);

    List<SVM0130PrintServicePartBO> get0KmJobCardPartList(Long serviceOrderId);

    SVM0130PrintBO get0KmServicePaymentHeaderData(Long serviceOrderId);

    List<SVM0130PrintServicePartBO> get0KmServicePaymentPartList(Long serviceOrderId);

    SVM0109PrintBO getServicePaymentData(Long serviceOrderId);

    SVM0109PrintBO getServicePaymentForDoData(Long serviceOrderId);

    SVM0109PrintBO getJobCardData(Long serviceOrderId);

    SVM0109PrintBO getJobCardForDoData(Long serviceOrderId);

    EInvoiceInvoiceBO getInvoiceinfoForService(Long orderId, String updateProgram);
    
    List<EInvoiceProductsBO> getServiceProductsModels(Long orderId);

}
