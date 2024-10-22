package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.common.EInvoiceProductsBO;
import com.a1stream.domain.bo.service.SVM0120PrintServiceJobBO;
import com.a1stream.domain.bo.service.SVM0120PrintServicePartBO;

public interface ServiceOrderItemOtherBrandRepositoryCustom {

    List<SVM0120PrintServiceJobBO> getOtherBrandJobCardJobList(Long serviceOrderId);

    List<SVM0120PrintServicePartBO> getOtherBrandJobCardPartList(Long serviceOrderId);

    List<SVM0120PrintServiceJobBO> getOtherBrandPaymentJobList(Long serviceOrderId);

    List<SVM0120PrintServicePartBO> getOtherBrandPaymentPartList(Long serviceOrderId);

    List<EInvoiceProductsBO> getServiceProductsForOtherBrandModels(Long orderId,String type);
}
