package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM021401BO;
import com.a1stream.domain.form.parts.SPM021401Form;
import com.a1stream.domain.form.parts.SPM021402Form;

public interface ReturnRequestListRepositoryCustom {

    List<SPM021401BO> getReturnRequestListList(SPM021401Form form, PJUserDetails uc);

    SPM021401BO getReturnRequestList(SPM021402Form form, PJUserDetails uc);
}
