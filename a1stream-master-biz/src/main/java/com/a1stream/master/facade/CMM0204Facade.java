package com.a1stream.master.facade;

import org.springframework.stereotype.Component;

import com.a1stream.domain.bo.master.CMM020401BO;
import com.a1stream.domain.vo.CmmMstOrganizationVO;
import com.a1stream.domain.vo.MstOrganizationVO;
import com.a1stream.master.service.CMM0204Service;

import jakarta.annotation.Resource;

@Component
public class CMM0204Facade {

    @Resource
    private CMM0204Service cmm0204Service;

    public CMM020401BO getCompanyInfo(String siteId) {

        return cmm0204Service.getCompanyInfo(siteId);
    }

    public void updateCompanyInfo(String siteId
                                , CMM020401BO companyInfo) {

        MstOrganizationVO mstOrganizationVO = cmm0204Service.findMstOrganizationVO(siteId);

        CmmMstOrganizationVO cmmMstOrganizationVO = cmm0204Service.findCmmMstOrganizationVO(siteId);

        updateMstOrganization(companyInfo, mstOrganizationVO);

        updateCmmMstOrganization(companyInfo, cmmMstOrganizationVO);

        cmm0204Service.updateCompanyInfo(mstOrganizationVO, cmmMstOrganizationVO);
    }

    /**
     * @param companyInfo
     * @param cmmMstOrganizationVO
     */
    private void updateCmmMstOrganization(CMM020401BO companyInfo, CmmMstOrganizationVO cmmMstOrganizationVO) {

        if(cmmMstOrganizationVO != null) {

            cmmMstOrganizationVO.setOrganizationAbbr(companyInfo.getOrganizationAbbr());
            cmmMstOrganizationVO.setProvinceId(companyInfo.getProvinceId());
            cmmMstOrganizationVO.setProvinceNm(companyInfo.getProvinceNm());
            cmmMstOrganizationVO.setCityId(companyInfo.getCityId());
            cmmMstOrganizationVO.setCityNm(companyInfo.getCityNm());
            cmmMstOrganizationVO.setAddress1(companyInfo.getAddress1());
            cmmMstOrganizationVO.setAddress2(companyInfo.getAddress2());
            cmmMstOrganizationVO.setPostCode(companyInfo.getPostCode());
            cmmMstOrganizationVO.setContactTel(companyInfo.getContactTel());
        }
    }

    /**
     * @param companyInfo
     * @param mstOrganizationVO
     */
    private void updateMstOrganization(CMM020401BO companyInfo, MstOrganizationVO mstOrganizationVO) {

        if (mstOrganizationVO != null) {

            mstOrganizationVO.setOrganizationAbbr(companyInfo.getOrganizationAbbr());
            mstOrganizationVO.setProvinceId(companyInfo.getProvinceId());
            mstOrganizationVO.setProvinceNm(companyInfo.getProvinceNm());
            mstOrganizationVO.setCityId(companyInfo.getCityId());
            mstOrganizationVO.setCityNm(companyInfo.getCityNm());
            mstOrganizationVO.setAddress1(companyInfo.getAddress1());
            mstOrganizationVO.setAddress2(companyInfo.getAddress2());
            mstOrganizationVO.setContactFax(companyInfo.getContactFax());
            mstOrganizationVO.setPostCode(companyInfo.getPostCode());
            mstOrganizationVO.setContactTel(companyInfo.getContactTel());
        }
    }
}
