package com.a1stream.common.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.manager.RoleManager;
import com.a1stream.common.model.BaseHelperBO;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.CmmHelperBO;
import com.a1stream.domain.bo.common.RoleBO;
import com.a1stream.domain.entity.MstCodeInfo;
import com.a1stream.domain.repository.AbcDefinitionInfoRepository;
import com.a1stream.domain.repository.BinTypeRepository;
import com.a1stream.domain.repository.CmmGeorgaphyRepository;
import com.a1stream.domain.repository.CmmMstOrganizationRepository;
import com.a1stream.domain.repository.CmmPersonRepository;
import com.a1stream.domain.repository.MstBrandRepository;
import com.a1stream.domain.repository.MstCodeInfoRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstOrganizationRepository;
import com.a1stream.domain.repository.MstProductCategoryRepository;
import com.a1stream.domain.repository.SysRoleRepository;
import com.a1stream.domain.repository.SysUserAuthorityRepository;
import com.a1stream.domain.repository.WorkzoneRepository;
import com.a1stream.domain.vo.BinTypeVO;
import com.a1stream.domain.vo.CmmGeorgaphyVO;
import com.a1stream.domain.vo.CmmMstOrganizationVO;
import com.a1stream.domain.vo.MstBrandVO;
import com.a1stream.domain.vo.MstCodeInfoVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductCategoryVO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.a1stream.domain.vo.WorkzoneVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Service
public class HelperService {

    @Resource
    private MstCodeInfoRepository mstCodeInfoRepo;

    @Resource
    private BinTypeRepository binTypeRepository;

    @Resource
    private MstBrandRepository mstBrandRepository;

    @Resource
    private SysUserAuthorityRepository sysUserAuthorityRepository;

    @Resource
    private MstProductCategoryRepository mstProductCategoryRepository;

    @Resource
    private WorkzoneRepository workzoneRepository;

    @Resource
    private CmmGeorgaphyRepository cmmGeorgaphyRepository;

    @Resource
    private AbcDefinitionInfoRepository abcDefinitionInfoRepository;

    @Resource
    private MstFacilityRepository facilityRepo;

    @Resource
    private CmmPersonRepository personRepo;

    @Resource
    private MstOrganizationRepository orgRepo;

    @Resource
    private SysRoleRepository sysRoleRepo;

    @Resource
    private CmmMstOrganizationRepository cmmMstOrgRepo;

    @Resource
    private RoleManager roleManager;

    public List<BaseHelperBO> getMstCodeInfoByCodeId(String codeId) {

        List<MstCodeInfo> mstCodeInfos = mstCodeInfoRepo.findByCodeId(codeId);

        return BeanMapUtils.mapListTo(mstCodeInfos, BaseHelperBO.class);
    }

    public List<CmmMstOrganizationVO> getDealerList(List<String> organizationCdList) {

        return BeanMapUtils.mapListTo(cmmMstOrgRepo.findByOrganizationCdNotIn(organizationCdList), CmmMstOrganizationVO.class);
    }

    public List<MstFacilityVO> getToPointList(String dealerCd) {

        return BeanMapUtils.mapListTo(facilityRepo.findBySiteId(dealerCd), MstFacilityVO.class);
    }

    public List<BaseHelperBO> getMstCodeInfoByCodeIds(String siteId, Set<String> codeIds) {

        List<MstCodeInfo> mstCodeInfos;
        if (StringUtils.isEmpty(siteId)) {
            mstCodeInfos = mstCodeInfoRepo.findByCodeIdIn(codeIds);
        } else {
            mstCodeInfos = mstCodeInfoRepo.findBySiteIdAndCodeIdIn(siteId, codeIds);
        }

        return BeanMapUtils.mapListTo(mstCodeInfos, BaseHelperBO.class);
    }

    public List<CmmHelperBO> getPointList(PJUserDetails uc, BaseVLForm model) {

    	return facilityRepo.getPointList(uc, model);
    }

    public List<CmmHelperBO> getEmployeeList(BaseVLForm model, String siteId) {

    	return personRepo.getEmployeeList(model, siteId);
    }

    public List<CmmHelperBO> getSupplierList(String siteId, BaseVLForm model) {

    	return orgRepo.getSupplierList(siteId, model);
    }

    public List<RoleBO> getUserRoleList(String siteId, BaseVLForm model) {

        String roleType = roleManager.getRoleTypeByDealerCode(siteId);

    	return sysRoleRepo.getUserRoleList(roleType, model);
    }

    public List<BinTypeVO> getBinTypeBySiteId(String siteId){

        return BeanMapUtils.mapListTo(binTypeRepository.findBySiteIdOrderByDescription(siteId), BinTypeVO.class);
    }

    public List<MstBrandVO> getBrandList(String siteId){

        return BeanMapUtils.mapListTo(mstBrandRepository.findBySiteIdOrderByBrandId(siteId), MstBrandVO.class);
    }

    public SysUserAuthorityVO getUserAuthorityByUserId(String userId) {

        return BeanMapUtils.mapTo(sysUserAuthorityRepository.findFirstByUserId(userId), SysUserAuthorityVO.class);
    }

    public List<BaseHelperBO> getJudgementStausList(String type) {

        return mstCodeInfoRepo.getJudgementStausList(type);
    }

    public List<MstProductCategoryVO> getMstProductCategoryByCategoryType(String categoryType) {

        return BeanMapUtils.mapListTo(mstProductCategoryRepository.findByCategoryTypeOrderByCategoryNm(categoryType), MstProductCategoryVO.class);
    }

    public List<WorkzoneVO> getWorkZoneBySiteId(String siteId) {

        return BeanMapUtils.mapListTo(workzoneRepository.findBySiteId(siteId), WorkzoneVO.class);
    }

    public List<WorkzoneVO> getWorkZoneBySiteIdAndFacilityId(String siteId, Long facilityId) {

        return BeanMapUtils.mapListTo(workzoneRepository.findBySiteIdAndFacilityId(siteId, facilityId), WorkzoneVO.class);
    }

    public List<BaseHelperBO> findByCodeIdAndSiteIdOrderByKey1(String codeId, String siteId) {

        return BeanMapUtils.mapListTo(mstCodeInfoRepo.findByCodeIdAndSiteIdOrderByKey1(codeId, siteId), BaseHelperBO.class);
    }

    public List<CmmGeorgaphyVO> getCmmGeorgaphyByGeographyClassificationId(String geographyClassificationId) {

        return BeanMapUtils.mapListTo(cmmGeorgaphyRepository.findByGeographyClassificationId(geographyClassificationId), CmmGeorgaphyVO.class);
    }

    public List<CmmGeorgaphyVO> getCityByProvince(String clsId, Long provinceId) {

        return BeanMapUtils.mapListTo(cmmGeorgaphyRepository.getCityByProvince(clsId, provinceId), CmmGeorgaphyVO.class);
    }

    public List<MstProductCategoryVO> getProductCategoryList(String productClassificationId, String siteId) {

        return BeanMapUtils.mapListTo(mstProductCategoryRepository.findByProductClassificationAndSiteId(productClassificationId, CommonConstants.CHAR_DEFAULT_SITE_ID), MstProductCategoryVO.class);
    }

    public List<MstBrandVO> getDefaultFlagIsequalsYBrandList(String siteId, String defaultFlag){

        return BeanMapUtils.mapListTo(mstBrandRepository.findBySiteIdAndFlag(siteId, defaultFlag), MstBrandVO.class);
    }

    public List<BaseHelperBO> getScheduleTimeList(String siteId, String facilityId, String scheduleDate) {

        return mstCodeInfoRepo.getScheduleTimeList(siteId, facilityId, scheduleDate);
    }

    public List<MstCodeInfoVO> findByCodeIdAndCodeData2(String codeId, String codeData2) {

        return BeanMapUtils.mapListTo(mstCodeInfoRepo.findByCodeIdAndCodeData2(codeId, codeData2), MstCodeInfoVO.class);
    }

}
