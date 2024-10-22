package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.master.CMM050201BO;
import com.a1stream.domain.bo.master.CMM050301BO;
import com.a1stream.domain.form.master.CMM050301Form;

public interface MstProductCategoryRepositoryCustom {

    List<CMM050201BO> searchProductLargeGroupList();

    List<CMM050301BO> searchProductMiddleGroupList(CMM050301Form model);
}
