package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.master.CMM020801BO;
import com.a1stream.domain.form.master.CMM020801Form;

public interface OrganizationRelationRepositoryCustom {

    public List<CMM020801BO> findTraderInfoList(CMM020801Form form, String siteId);

}
