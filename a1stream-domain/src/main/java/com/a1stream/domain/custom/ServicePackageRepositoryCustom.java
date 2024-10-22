package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.master.CMM060201BO;
import com.a1stream.domain.form.master.CMM060201Form;

public interface ServicePackageRepositoryCustom {

    ValueListResultBO findPackageList(BaseVLForm model, String siteId);

    ValueListResultBO findServicePackageByMc(BaseVLForm model, String siteId);

    List<CMM060201BO> getSvPackageData(CMM060201Form model);

    Integer existServicePackage(String packageCd, String siteId);
}
