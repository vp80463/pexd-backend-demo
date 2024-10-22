package com.a1stream.ifs.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.entity.CmmMstOrganization;
import com.a1stream.domain.entity.CmmSiteMaster;
import com.a1stream.domain.repository.CmmMstOrganizationRepository;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.vo.CmmMstOrganizationVO;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
 * @author dong zhen
 */
@Service
public class SpDiscountRateService {

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    @Resource
    private CmmMstOrganizationRepository cmmMstOrganizationRepository;

    public List<CmmSiteMasterVO> getCmmSiteMasterListBySite(Set<String> dealerCdList) {

        List<CmmSiteMaster> cmmSiteMasterList = cmmSiteMasterRepository.findBySiteIdInAndActiveFlag(dealerCdList, CommonConstants.CHAR_Y);
        return BeanMapUtils.mapListTo(cmmSiteMasterList, CmmSiteMasterVO.class);
    }

    public List<CmmMstOrganizationVO> getOrganizationListByOrganizationCd(Set<String> organizationCdList) {

        List<CmmMstOrganization> mstOrganizationList = cmmMstOrganizationRepository.findByOrganizationCdIn(organizationCdList);
        return BeanMapUtils.mapListTo(mstOrganizationList, CmmMstOrganizationVO.class);
    }

    public void updateMstOrganization(List<CmmMstOrganizationVO> updateList) {

        List<CmmMstOrganization> cmmMstOrganizationList = BeanMapUtils.mapListTo(updateList, CmmMstOrganization.class);
        cmmMstOrganizationRepository.saveInBatch(cmmMstOrganizationList);
    }
}
