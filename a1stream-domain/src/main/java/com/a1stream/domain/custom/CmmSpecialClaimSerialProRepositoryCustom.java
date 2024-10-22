package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.master.CMQ060303BO;
import com.a1stream.domain.form.master.CMQ060303Form;

public interface CmmSpecialClaimSerialProRepositoryCustom {

    List<CMQ060303BO> findCampaignResultInquiryList(CMQ060303Form form );
}
