package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.data.domain.Page;

import com.a1stream.domain.bo.unit.SVM010603BO;
import com.a1stream.domain.form.unit.SVM010603Form;

public interface CmmLeadManagementUnitRepositoryCustom {

    Page<SVM010603BO> pageMcSalesLeadData(SVM010603Form form, String siteId, String dealerCd);

    List<SVM010603BO> listMcSalesLeadData(SVM010603Form form, String siteId, String dealerCd);
}
