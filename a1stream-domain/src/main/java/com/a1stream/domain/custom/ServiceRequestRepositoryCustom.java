package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.service.SVM020101BO;
import com.a1stream.domain.bo.service.SVM0201PrintBO;
import com.a1stream.domain.bo.service.SVM0201PrintDetailBO;
import com.a1stream.domain.bo.service.SVM0202PrintBO;
import com.a1stream.domain.form.service.SVM020101Form;

public interface ServiceRequestRepositoryCustom {

    List<SVM020101BO> findServiceRequestList(SVM020101Form form);

    SVM0202PrintBO getServiceExpensesCouponStatementPrintDetailData(String siteId, String paymentMonth);

    SVM0201PrintBO getPartsClaimTagPrintHeaderData(Long serviceRequestId);

    List<SVM0201PrintDetailBO> getPartsClaimTagPrintDetailList(Long serviceRequestId);

    SVM0201PrintBO getPartsClaimForBatteryClaimTagPrintHeaderData(Long serviceRequestId);
}