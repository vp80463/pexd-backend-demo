package com.a1stream.master.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.SpecialClaimProblemCategory;
import com.a1stream.domain.bo.master.CMQ060301BO;
import com.a1stream.domain.bo.master.CMQ060303BO;
import com.a1stream.domain.form.master.CMQ060301Form;
import com.a1stream.domain.form.master.CMQ060302Form;
import com.a1stream.domain.form.master.CMQ060303Form;
import com.a1stream.domain.repository.CmmSpecialClaimProblemRepository;
import com.a1stream.domain.repository.CmmSpecialClaimRepairRepository;
import com.a1stream.domain.repository.CmmSpecialClaimRepository;
import com.a1stream.domain.repository.CmmSpecialClaimSerialProRepository;
import com.a1stream.domain.repository.CmmSpecialClaimStampingRepository;
import com.a1stream.domain.vo.CmmSpecialClaimStampingVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@Service
public class CMQ0603Service {

    @Resource
    private CmmSpecialClaimRepository cmmSpecialClaimRepository;

    @Resource
    private CmmSpecialClaimRepairRepository cmmSpecialClaimRepairRepository;

    @Resource
    private CmmSpecialClaimProblemRepository cmmSpecialClaimProblemRepository;

    @Resource
    private CmmSpecialClaimStampingRepository cmmSpecialClaimStampingRepository;

    @Resource
    private CmmSpecialClaimSerialProRepository cmmSpecialClaimSerialProRepository;

    public List<CMQ060301BO> findCampaignInquiryList(CMQ060301Form form) {

        return cmmSpecialClaimRepository.findCampaignInquiryList(form);
    }

    public List<CMQ060303BO> findCampaignResultInquiryList(CMQ060303Form form) {

        return cmmSpecialClaimSerialProRepository.findCampaignResultInquiryList(form);
    }

    public CMQ060302Form getInitList(CMQ060302Form form) {

        CMQ060302Form initForm = new CMQ060302Form();

        // Repair Detail
        initForm.setRepairDetailOne(cmmSpecialClaimRepairRepository.findRepairDetailList(form, ProductClsType.PART.getCodeDbid()));
        initForm.setRepairDetailTwo(cmmSpecialClaimRepairRepository.findRepairDetailList(form, ProductClsType.SERVICE.getCodeDbid()));
        // Problem Detail
        initForm.setProblemDetailOne(cmmSpecialClaimProblemRepository.findProblemDetailList(form, SpecialClaimProblemCategory.SYMPTOM));
        initForm.setProblemDetailTwo(cmmSpecialClaimProblemRepository.findProblemDetailList(form, SpecialClaimProblemCategory.CONDITION));
        // Stamping Style Detail
        initForm.setStampingStyleDetail(BeanMapUtils.mapListTo(cmmSpecialClaimStampingRepository.findBySiteIdAndSpecialClaimId(CommonConstants.CHAR_DEFAULT_SITE_ID, form.getCampaignId()), CmmSpecialClaimStampingVO.class));

        return initForm;
    }
}