package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.master.CMM071601BO;
import com.a1stream.domain.form.master.CMM071601Form;

public interface QueueTaxAuthorityRepositoryCustom {

    List<CMM071601BO> getInvoiceCheckResultByTaxAuthority(CMM071601Form form);

}
