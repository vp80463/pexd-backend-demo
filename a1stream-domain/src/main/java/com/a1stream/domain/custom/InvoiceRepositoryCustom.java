package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.data.domain.Page;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.PartsSalesReturnInvoiceForFinancePrintBO;
import com.a1stream.domain.bo.parts.PartsSalesReturnInvoiceForFinancePrintDetailBO;
import com.a1stream.domain.bo.parts.SPM020103PrintBO;
import com.a1stream.domain.bo.parts.SPM020201PrintBO;
import com.a1stream.domain.bo.parts.SPM020201PrintDetailBO;
import com.a1stream.domain.bo.parts.SPM020202FunctionBO;
import com.a1stream.domain.bo.parts.SPQ020301BO;
import com.a1stream.domain.bo.parts.SPQ020402BO;
import com.a1stream.domain.form.parts.SPM020202Form;
import com.a1stream.domain.form.parts.SPQ020301Form;
import com.a1stream.domain.form.parts.SPQ020401Form;

public interface InvoiceRepositoryCustom {

    SPM020201PrintBO getPartsSalesReturnInvoiceForFinanceData(Long invoiceId);

    List<SPM020201PrintDetailBO> getPartsSalesReturnInvoiceForFinanceDetailList(Long invoiceId);

    Page<SPQ020301BO> getInvoiceList(SPQ020301Form form, PJUserDetails uc);

    List<SPM020103PrintBO> getPartsSalesInvoiceForDOList(List<Long> invoiceIds);

    List<SPQ020402BO> searchInvoiceInfo(SPQ020401Form model);

    List<SPM020202FunctionBO> getSalesReturnHistoryHeaderList(SPM020202Form form, String siteId);

    PartsSalesReturnInvoiceForFinancePrintBO getCommonPartsSalesReturnInvoiceForFinanceData(Long invoiceId);

    List<PartsSalesReturnInvoiceForFinancePrintDetailBO> getCommonPartsSalesReturnInvoiceForFinanceDetailList(Long invoiceId);
}
