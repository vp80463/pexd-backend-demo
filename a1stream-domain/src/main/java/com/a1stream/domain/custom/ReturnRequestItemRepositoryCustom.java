package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM021402BO;
import com.a1stream.domain.form.parts.SPM021402Form;

public interface ReturnRequestItemRepositoryCustom {

    List<SPM021402BO> getReturnRequestItemList(SPM021402Form form, PJUserDetails uc);

}
