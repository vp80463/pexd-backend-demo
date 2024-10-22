package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.master.CMQ060301BO;
import com.a1stream.domain.form.master.CMQ060301Form;

public interface CmmSpecialClaimRepositoryCustom {

    ValueListResultBO findServiceSpList(BaseVLForm model);

    List<CMQ060301BO> findCampaignInquiryList(CMQ060301Form form);
}
