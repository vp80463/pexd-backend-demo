package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM020104BO;
import com.a1stream.domain.form.parts.SPM020104Form;

public interface PickingItemRepositoryCustom {

    List<SPM020104BO> getPickingItemList(SPM020104Form form, PJUserDetails uc);

}
