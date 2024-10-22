package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.master.CMM071601BO;
import com.a1stream.domain.form.master.CMM071601Form;

public interface QueueEinvoiceRepositoryCustom {

    List<CMM071601BO> getInvoiceCheckResultByInvoice(CMM071601Form form);

    List<CMM071601BO> getInvoiceCheckResult(CMM071601Form form);

}
