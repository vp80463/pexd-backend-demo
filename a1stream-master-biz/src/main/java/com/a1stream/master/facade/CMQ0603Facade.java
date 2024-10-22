package com.a1stream.master.facade;

import java.util.List;

import org.springframework.stereotype.Component;

import com.a1stream.domain.bo.master.CMQ060301BO;
import com.a1stream.domain.bo.master.CMQ060303BO;
import com.a1stream.domain.form.master.CMQ060301Form;
import com.a1stream.domain.form.master.CMQ060302Form;
import com.a1stream.domain.form.master.CMQ060303Form;
import com.a1stream.master.service.CMQ0603Service;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@Component
public class CMQ0603Facade {

    @Resource
    private CMQ0603Service cmq0603Service;

    public List<CMQ060301BO> findCampaignInquiryList(CMQ060301Form form) {

        return cmq0603Service.findCampaignInquiryList(form);
    }

    public CMQ060302Form init060302Screen(CMQ060302Form form) {

        CMQ060302Form initForm = cmq0603Service.getInitList(form);

        return initForm;
    }

    public List<CMQ060303BO> inital0603Screen(CMQ060303Form form) {

        return cmq0603Service.findCampaignResultInquiryList(form);
    }
}