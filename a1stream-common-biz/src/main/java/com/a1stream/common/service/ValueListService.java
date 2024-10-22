package com.a1stream.common.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.FacilityType;
import com.a1stream.common.manager.RoleManager;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ConsumerVLForm;
import com.a1stream.common.model.DemandVLForm;
import com.a1stream.common.model.LocationVLBO;
import com.a1stream.common.model.LocationVLForm;
import com.a1stream.common.model.PartsVLBO;
import com.a1stream.common.model.PartsVLForm;
import com.a1stream.common.model.ServiceJobVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.common.DemandBO;
import com.a1stream.domain.repository.CmmConditionRepository;
import com.a1stream.domain.repository.CmmConsumerRepository;
import com.a1stream.domain.repository.CmmMstOrganizationRepository;
import com.a1stream.domain.repository.CmmPersonRepository;
import com.a1stream.domain.repository.CmmSectionRepository;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.CmmServiceDemandRepository;
import com.a1stream.domain.repository.CmmServiceGroupItemRepository;
import com.a1stream.domain.repository.CmmServiceHistoryRepository;
import com.a1stream.domain.repository.CmmServiceJobForDORepository;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.CmmSpecialClaimRepository;
import com.a1stream.domain.repository.CmmSymptomRepository;
import com.a1stream.domain.repository.CmmUnitPromotionListRepository;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.MstCodeInfoRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstOrganizationRepository;
import com.a1stream.domain.repository.MstProductRelationRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.ProductTaxRepository;
import com.a1stream.domain.repository.ServicePackageRepository;
import com.a1stream.domain.repository.SysRoleRepository;
import com.a1stream.domain.repository.SysUserRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.CmmSymptomVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

@Service
public class ValueListService {

    @Resource
    private SysRoleRepository sysRoleRepo;

    @Resource
    private CmmPersonRepository cmmPersonRepo;

    @Resource
    private MstOrganizationRepository mstOrganizationRepository;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    @Resource
    private SysUserRepository sysUserRepository;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private CmmSectionRepository cmmSectionRepository;

    @Resource
    private ServicePackageRepository servicePackageRepository;

    @Resource
    private CmmSerializedProductRepository cmmSerializedProductRepository;

    @Resource
    private CmmServiceDemandRepository cmmServiceDemandRepository;

    @Resource
    private CmmServiceHistoryRepository cmmServiceHistoryRepository;

    @Resource
    private CmmConsumerRepository cmmConsumerRepository;

    @Resource
    private CmmSymptomRepository cmmSymptomRepository;

    @Resource
    private CmmServiceGroupItemRepository cmmServiceGroupItemRepository;

    @Resource
    private CmmConditionRepository cmmConditionRepository;

    @Resource
    private CmmServiceJobForDORepository cmmServiceJobForDORepository;

    @Resource
    private CmmSpecialClaimRepository cmmSpecialClaimRepository;

    @Resource
    private ProductInventoryRepository productInventoryRepository;

    @Resource
    private LocationRepository locationRepository;

    @Resource
    private MstProductRelationRepository mstProductRelationRepository;

    @Resource
    private ProductStockStatusRepository productStockStatusRepository;

    @Resource
    private ProductTaxRepository productTaxRepository;

    @Resource
    private MstCodeInfoRepository mstCodeInfoRepository;

    @Resource
    private SystemParameterRepository systemParameterRepository;

    @Resource
    private CmmMstOrganizationRepository cmmMstOrganizationRepository;

    @Resource
    private RoleManager roleManager;

    @Resource
    private CmmUnitPromotionListRepository cmmUnitPromotionListRepository;

    public ValueListResultBO findUserRoleList(String siteId, BaseVLForm model) {

        String roleType = roleManager.getRoleTypeByDealerCode(siteId);

        return sysRoleRepo.findUserRoleList(roleType, model);
    }

    public ValueListResultBO findEmployeeList(BaseVLForm model, String siteId) {

        return cmmPersonRepo.findEmployeeList(model, siteId);
    }

    public ValueListResultBO findPointList(PJUserDetails uc, BaseVLForm model) {

        //管理员时，获取当前site下所有point
        if (StringUtils.equals(uc.getCompanyCode(), CommonConstants.CHAR_DEFAULT_SITE_ID)
                || StringUtils.equals(uc.getCompanyCode(), CommonConstants.CHAR_YMSLX_SITE_ID)) {

            model.setArg0(FacilityType.SHOP_ALL);
        }

        return mstFacilityRepository.findPointList(uc, model);
    }

    public ValueListResultBO findSupplierList(String siteId, BaseVLForm model) {

        return mstOrganizationRepository.findSupplierList(siteId, model);
    }

    public ValueListResultBO findDealerList(BaseVLForm model){
       return cmmSiteMasterRepository.findDealerList(model);
    }

    public ValueListResultBO findUserValueList(BaseVLForm model) {

        return sysUserRepository.findUserValueList(model);
    }

    public ValueListResultBO findModelList(BaseVLForm model) {

        return mstProductRepository.findModelList(model);
    }

    public ValueListResultBO findSdModelList(BaseVLForm model) {

        return mstProductRepository.findSdModelList(model);
    }
    public ValueListResultBO findPartsList(PartsVLForm model, String siteId) {

        return mstProductRepository.findPartsList(model, siteId);
    }

    public ValueListResultBO findYamahaPartsList(PartsVLForm model) {

        return mstProductRepository.findYamahaPartsList(model);
    }

    public List<PartsVLBO> findSupersedingPartsIdList(List<Long> productIds) {
        return mstProductRelationRepository.findSupersedingPartsIdList(productIds);
    }

    public List<PartsVLBO> findMainLocationIdList(List<Long> productIds,String siteId, Long facilityId, boolean yamahaFlag) {
    	siteId = yamahaFlag? CommonConstants.CHAR_DEFAULT_SITE_ID : siteId;
        return productInventoryRepository.findMainLocationIdList(productIds, siteId, facilityId);
    }

    public List<PartsVLBO> findOnHandQtyList(List<Long> productIds, String siteId, Long facilityId, boolean yamahaFlag) {
    	siteId = yamahaFlag? CommonConstants.CHAR_DEFAULT_SITE_ID : siteId;
        return productStockStatusRepository.findOnHandQtyList(productIds, siteId, facilityId);
    }

    public List<PartsVLBO> findProductTaxList(List<Long> productIds) {
        return productTaxRepository.findProductTaxList(productIds);
    }

    public ValueListResultBO findSectionList(BaseVLForm model) {

        return cmmSectionRepository.findSectionList(model);
    }

    public ValueListResultBO findPackageList(BaseVLForm model, String siteId) {

        return servicePackageRepository.findPackageList(model, siteId);
    }

    public List<DemandBO> findSerializedProductList(DemandVLForm model){

        return cmmSerializedProductRepository.searchSerializedProductList(model);
    }

    public List<DemandBO> findServiceDemandList(DemandVLForm model){

        return cmmServiceDemandRepository.searchServiceDemandList(model);
    }

    public ValueListResultBO findConsumerByUnitList(ConsumerVLForm model, String siteId) {

        return cmmConsumerRepository.findConsumerByUnitList(model, siteId);
    }

    public ValueListResultBO findFaultCodeList(BaseVLForm model) {

        return cmmSymptomRepository.findByProductSectionId(model);
    }

    public ValueListResultBO findFaultDescriptionList(BaseVLForm model) {

        return cmmConditionRepository.findCmmConditionList(model);
    }

    public ValueListResultBO findServiceJobList(BaseVLForm model) {

        return mstProductRepository.findServiceJobList(model);
    }

    public ValueListResultBO findServiceJobByModelList(ServiceJobVLForm model, Set<String> modelCode) {

        return cmmServiceGroupItemRepository.findServiceJobByModelList(model, modelCode);
    }

    public ValueListResultBO findServiceJobByModelTypeList(ServiceJobVLForm model) {
        return cmmServiceJobForDORepository.findServiceJobByModelTypeList(model);
    }

    public ValueListResultBO findServiceSpList(BaseVLForm model) {
        return cmmSpecialClaimRepository.findServiceSpList(model);
    }
    public Page<LocationVLBO> findLocationListByProductInventoty(LocationVLForm model,String siteId) {

        return productInventoryRepository.findLocationList(model, siteId);
    }
    public Page<LocationVLBO> findLocationListByLocation(LocationVLForm model,String siteId,List<String> locationTypes) {

        return locationRepository.findLocationList(model, siteId,locationTypes);
    }

    public List<CmmSymptomVO> findByProductSectionId(Long productSectionId){

        return BeanMapUtils.mapListTo(cmmSymptomRepository.findByProductSectionId(productSectionId), CmmSymptomVO.class);
    }

    public SystemParameterVO findSysParamaterById(String jobTaxrate) {

        return BeanMapUtils.mapTo(systemParameterRepository.findBySystemParameterTypeId(jobTaxrate), SystemParameterVO.class);
    }

    public List<ProductInventoryVO> findByLocationIdAndPrimaryFlag(String siteId,Long pointId,Set<Long> locationIds, String primaryFlag){

        return BeanMapUtils.mapListTo(productInventoryRepository.findBySiteIdAndFacilityIdAndLocationIdInAndPrimaryFlag(siteId,pointId,locationIds, primaryFlag), ProductInventoryVO.class);
    }

    /**
     * @author Peng Zhengan
     */
    public ValueListResultBO findCustomerValueList(BaseVLForm model, String siteId) {

        return cmmMstOrganizationRepository.findCustomerValueList(model, siteId);
    }

    public ValueListResultBO findPromotionValueList(BaseVLForm model) {

        return cmmUnitPromotionListRepository.findPromotionValueList(model);
    }

    public ValueListResultBO findServicePackageList(BaseVLForm model, String siteId){

        return servicePackageRepository.findServicePackageByMc(model, siteId);
    }

    public List<ProductInventoryVO> findMainLocationByProInv(Long facilityId, Long productId, String siteId, List<Long> locationIds){

        return BeanMapUtils.mapListTo(productInventoryRepository.findBySiteIdAndProductIdAndFacilityIdAndLocationIdIn(siteId,productId,facilityId,locationIds), ProductInventoryVO.class);
    }
}
