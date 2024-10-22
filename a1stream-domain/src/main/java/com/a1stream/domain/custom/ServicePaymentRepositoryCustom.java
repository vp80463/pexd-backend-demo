package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.service.SVM020201BO;
import com.a1stream.domain.bo.service.SVM020202BO;
import com.a1stream.domain.bo.service.SVM0202PrintBO;
import com.a1stream.domain.form.service.SVM020201Form;
import com.a1stream.domain.form.service.SVM020202Form;

public interface ServicePaymentRepositoryCustom {

    List<SVM020201BO> findServicePaymentList(SVM020201Form form, String siteId);

    SVM020201BO getSpDetailHeader(SVM020202Form form);

    List<SVM020202BO> getSpDetailList(SVM020202Form form);

    Integer getCheckCountByInvoiceNoAndSerialNo(SVM020202Form form, String siteId);

    Integer getCheckCountByInvoiceNo(SVM020202Form form, String siteId);

    SVM0202PrintBO getServiceExpensesClaimStatementPrintData(Long paymentId);

    SVM0202PrintBO getServiceExpensesClaimStatementForEVPrintData(Long paymentId);

    SVM0202PrintBO getServiceExpensesCouponStatementPrintData(Long paymentId);

}