package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.master.CMQ070101BO;
import com.a1stream.domain.bo.master.CMQ070102BO;
import com.a1stream.domain.form.master.CMQ070101Form;

public interface SysUserRepositoryCustom {

    ValueListResultBO findUserValueList(BaseVLForm model);

    List<CMQ070101BO> findUserList(CMQ070101Form model, String siteId);

    CMQ070102BO getUserDetail(String userId);
}
