package com.a1stream.master.service;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.master.CMM020401BO;
import com.a1stream.domain.entity.CmmMstOrganization;
import com.a1stream.domain.entity.MstOrganization;
import com.a1stream.domain.repository.CmmMstOrganizationRepository;
import com.a1stream.domain.repository.MstOrganizationRepository;
import com.a1stream.domain.vo.CmmMstOrganizationVO;
import com.a1stream.domain.vo.MstOrganizationVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class CMM0204Service {

    @Resource
    private MstOrganizationRepository mstOrganizationRepo;

    @Resource
    private CmmMstOrganizationRepository cmmMstOrganizationRepo;

    public CMM020401BO getCompanyInfo(String siteId) {

        return BeanMapUtils.mapTo(mstOrganizationRepo.findBySiteIdAndOrganizationCd(siteId, siteId), CMM020401BO.class);
    }

    public MstOrganizationVO findMstOrganizationVO(String siteId) {

        return BeanMapUtils.mapTo(mstOrganizationRepo.findBySiteIdAndOrganizationCd(siteId, siteId), MstOrganizationVO.class);
    }

    public CmmMstOrganizationVO findCmmMstOrganizationVO(String siteId) {

        return BeanMapUtils.mapTo(cmmMstOrganizationRepo.findBySiteIdAndOrganizationCd(CommonConstants.CHAR_DEFAULT_SITE_ID, siteId), CmmMstOrganizationVO.class);
    }

    public void updateCompanyInfo(MstOrganizationVO mstOrganizationVO
                                , CmmMstOrganizationVO cmmMstOrganizationVO) {

        if(cmmMstOrganizationVO != null) {

            cmmMstOrganizationRepo.saveWithVersionCheck(BeanMapUtils.mapTo(cmmMstOrganizationVO, CmmMstOrganization.class));
        }

        if(mstOrganizationVO != null) {

            mstOrganizationRepo.saveWithVersionCheck(BeanMapUtils.mapTo(mstOrganizationVO, MstOrganization.class));
        }
    }
}
