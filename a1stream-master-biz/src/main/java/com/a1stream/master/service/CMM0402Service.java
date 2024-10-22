package com.a1stream.master.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.master.CMM040201BO;
import com.a1stream.domain.entity.CmmSection;
import com.a1stream.domain.entity.CmmSymptom;
import com.a1stream.domain.form.master.CMM040201Form;
import com.a1stream.domain.form.master.CMM040202Form;
import com.a1stream.domain.repository.CmmSectionRepository;
import com.a1stream.domain.repository.CmmSymptomRepository;
import com.a1stream.domain.vo.CmmSectionVO;
import com.a1stream.domain.vo.CmmSymptomVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@Service
public class CMM0402Service {

    @Resource
    private CmmSectionRepository cmmSectionRepository;

    @Resource
    private CmmSymptomRepository cmmSymptomRepository;

    public List<CMM040201BO> findSectionInfoInquiryList(CMM040201Form form) {

        return cmmSectionRepository.findSectionInfoInquiryList(form);
    }

    public List<CmmSymptomVO> findCmmSymptomVOList(CMM040202Form form) {

        return BeanMapUtils.mapListTo(cmmSymptomRepository.findBySiteIdAndProductSectionId(CommonConstants.CHAR_DEFAULT_SITE_ID, form.getSectionId()), CmmSymptomVO.class);
    }

    public CmmSectionVO findCmmSectionVO(Long sectionId) {

        return BeanMapUtils.mapTo(cmmSectionRepository.findByProductSectionIdAndSiteId(sectionId, CommonConstants.CHAR_DEFAULT_SITE_ID), CmmSectionVO.class);
    }

    public Integer getCmmSectionCount(CMM040202Form form) {

        return cmmSectionRepository.getCmmSectionCount(form, CommonConstants.CHAR_DEFAULT_SITE_ID);
    }

    public void newConfirm(CmmSectionVO cmmSectionVO, List<CmmSymptomVO> cmmSymptomVOs) {

        if (!ObjectUtils.isEmpty(cmmSectionVO)) {
            cmmSectionRepository.save(BeanMapUtils.mapTo(cmmSectionVO, CmmSection.class));
        }

        cmmSymptomRepository.saveInBatch(BeanMapUtils.mapListTo(cmmSymptomVOs, CmmSymptom.class));
    }

    public void updateConfirm(CmmSectionVO cmmSectionVO
                             ,List<CmmSymptomVO> insertcmmSymptomVOs
                             ,List<CmmSymptomVO> updatecmmSymptomVOs
                             ,List<Long> deletecmmSymptomIds) {

        if (!ObjectUtils.isEmpty(cmmSectionVO)) {
            cmmSectionRepository.save(BeanMapUtils.mapTo(cmmSectionVO, CmmSection.class));
        }

        cmmSymptomRepository.saveInBatch(BeanMapUtils.mapListTo(insertcmmSymptomVOs, CmmSymptom.class));
        cmmSymptomRepository.saveInBatch(BeanMapUtils.mapListTo(updatecmmSymptomVOs, CmmSymptom.class));
        cmmSymptomRepository.deleteAllByIdInBatch(deletecmmSymptomIds);
    }
}