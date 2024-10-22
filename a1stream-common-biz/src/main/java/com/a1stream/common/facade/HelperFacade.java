package com.a1stream.common.facade;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.MstCodeConstants.SalesOrderStatus;
import com.a1stream.common.constants.MstCodeConstants.YmvnSite;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.FacilityType;
import com.a1stream.common.constants.PJConstants.FinishStatus;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.model.BaseHelperBO;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.CascaderBO;
import com.a1stream.common.model.CmmHelperBO;
import com.a1stream.common.model.ConstantsBO;
import com.a1stream.common.service.HelperService;
import com.a1stream.domain.bo.common.RoleBO;
import com.a1stream.domain.vo.BinTypeVO;
import com.a1stream.domain.vo.CmmGeorgaphyVO;
import com.a1stream.domain.vo.CmmMstOrganizationVO;
import com.a1stream.domain.vo.MstBrandVO;
import com.a1stream.domain.vo.MstCodeInfoVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductCategoryVO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.a1stream.domain.vo.WorkzoneVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Component
public class HelperFacade {

    @Resource
    private HelperService helperService;

    @Resource
    private ConstantsLogic constantsLogic;

    public Map<String, BaseHelperBO> getMstCodeBOMap(String siteId, String... args) {

        Map<String, BaseHelperBO> result = new HashMap<>();

        Set<String> codeIds = new HashSet<>();
        for(String arg : args) {
            codeIds.add(arg);
        }
        // mst_code_info
        List<BaseHelperBO> mstCodeInfos = helperService.getMstCodeInfoByCodeIds(siteId, codeIds);
        result = mstCodeInfos.stream().collect(Collectors.toMap(BaseHelperBO::getCodeDbid, Function.identity()));

        // pj_constants
        Set<String> mstCodeIds = mstCodeInfos.stream().map(BaseHelperBO::getCodeId).collect(Collectors.toSet());
        codeIds.removeAll(mstCodeIds);
        Map<String, Field[]> constantsFieldMap =  PJConstants.ConstantsFieldMap;
        for(String codeId : codeIds) {
            if (constantsFieldMap.containsKey(codeId)) {
                List<ConstantsBO> constants = constantsLogic.getConstantsData(constantsFieldMap.get(codeId));
                for(ConstantsBO item : constants) {
                    BaseHelperBO constantBO = BeanMapUtils.mapTo(item, BaseHelperBO.class);
                    result.put(item.getCodeDbid(), constantBO);
                }
            }
        }

        return result;
    }

    public Map<String, String> getMstCodeInfoMap(String... args) {

        Map<String, String> result = new HashMap<>();
        Set<String> codeIds = new HashSet<>();
        for(String arg : args) {
            codeIds.add(arg);
        }
        // mst_code_info
        List<BaseHelperBO> mstCodeInfos = helperService.getMstCodeInfoByCodeIds(null, codeIds);
        result = mstCodeInfos.stream().collect(Collectors.toMap(BaseHelperBO::getCodeDbid, BaseHelperBO::getCodeData1));
        // pj_constants
        Set<String> mstCodeIds = mstCodeInfos.stream().map(BaseHelperBO::getCodeId).collect(Collectors.toSet());
        codeIds.removeAll(mstCodeIds);
        Map<String, Field[]> constantsFieldMap =  PJConstants.ConstantsFieldMap;
        for(String codeId : codeIds) {
            if (constantsFieldMap.containsKey(codeId)) {
                List<ConstantsBO> constants = constantsLogic.getConstantsData(constantsFieldMap.get(codeId));
                for(ConstantsBO item : constants) {
                    result.put(item.getCodeDbid(), item.getCodeData1());
                }
            }
        }

        return result;
    }

    public Map<String, List<BaseHelperBO>> getMstCodeMapByCodeId(Set<String> codeIds) {

        Map<String, List<BaseHelperBO>> result = new HashMap<>();
        // mst_code_info
        List<BaseHelperBO> mstCodeInfos = helperService.getMstCodeInfoByCodeIds(null, codeIds);
        result = mstCodeInfos.stream().sorted(Comparator.comparing(BaseHelperBO::getKey1)).collect(Collectors.groupingBy(BaseHelperBO::getCodeId));
        // pj_constants
        Set<String> mstCodeIds = mstCodeInfos.stream().map(BaseHelperBO::getCodeId).collect(Collectors.toSet());
        codeIds.removeAll(mstCodeIds);
        Map<String, Field[]> constantsFieldMap =  PJConstants.ConstantsFieldMap;
        for(String codeId : codeIds) {
            if (constantsFieldMap.containsKey(codeId)) {
                List<ConstantsBO> constants = constantsLogic.getConstantsData(constantsFieldMap.get(codeId));
                List<BaseHelperBO> constantBO = BeanMapUtils.mapListTo(constants, BaseHelperBO.class);
                result.put(codeId, constantBO);
            }
        }
        return result;
    }

    public List<CmmHelperBO> employeeList(BaseVLForm model, String siteId) {

    	return helperService.getEmployeeList(model, siteId);
    }

    public List<CmmHelperBO> pointList(PJUserDetails uc, BaseVLForm model) {

        //管理员时，获取当前site下所有point
        if (StringUtils.equals(uc.getCompanyCode(), CommonConstants.CHAR_DEFAULT_SITE_ID)
                || StringUtils.equals(uc.getCompanyCode(), CommonConstants.CHAR_YMSLX_SITE_ID)) {

            model.setArg0(FacilityType.SHOP_ALL);
        }

    	return helperService.getPointList(uc, model);
    }

    public List<CmmHelperBO> supplierList(String siteId, BaseVLForm model) {

    	return helperService.getSupplierList(siteId, model);
    }

    public List<RoleBO> userRoleList(String siteId, BaseVLForm model) {

    	return helperService.getUserRoleList(siteId, model);
    }

    public List<BinTypeVO> getBinTypeBySiteId(String siteId){

        return helperService.getBinTypeBySiteId(siteId);
    }

    public List<ConstantsBO> getInventoryTransactionTypeByArg(String arg0) {

        List<ConstantsBO> inventoryTransactionTypeList = constantsLogic.getConstantsData(PJConstants.InventoryTransactionType.class.getDeclaredFields());

        if (StringUtils.isNotEmpty(arg0)) {

            String[] codeStrArry = arg0.split(CommonConstants.CHAR_SLASH);

            return inventoryTransactionTypeList.stream().filter(element -> Arrays.stream(codeStrArry).anyMatch(element.getCodeDbid()::equals)).toList();
        }else {

            return inventoryTransactionTypeList;
        }
    }

    public List<MstBrandVO> getBrandList(String agr0){

        List<MstBrandVO> brands = new ArrayList<>();
        List<MstBrandVO> brandList = helperService.getBrandList(CommonConstants.CHAR_DEFAULT_SITE_ID);
        if(StringUtils.isNotBlankText(agr0)) {

            for(MstBrandVO brand : brandList) {

                if(CommonConstants.CHAR_ZERO.equals(agr0) && !PJConstants.BrandType.YAMAHA.getCodeData1().equals(brand.getBrandNm()) && !PJConstants.BrandType.NOBIKE.getCodeData1().equals(brand.getBrandNm())) {

                    brands.add(brand);
                }else if(CommonConstants.CHAR_ONE.equals(agr0) && !PJConstants.BrandType.KIMOCO.getCodeData1().equals(brand.getBrandNm())) {

                    brands.add(brand);
                }else if(CommonConstants.CHAR_TWO.equals(agr0) && !PJConstants.BrandType.NOBIKE.getCodeData1().equals(brand.getBrandNm())) {

                    brands.add(brand);
                }
            }

            return brands;
        }

        return brandList;
    }

    public List<BaseHelperBO> getConsumerCategoryTypeList(String arg0, String arg1, String arg2) {

        List<BaseHelperBO> consumerCategoryTypes = new ArrayList<>();
        List<BaseHelperBO> consumerCategoryTypesList = helperService.getMstCodeInfoByCodeId(PJConstants.ConsumerType.CODE_ID);

        if(StringUtils.isBlankText(arg0) || !PJConstants.ConsumerType.BIKEPURCHASECUSTOMER.equals(arg1)) {

            for(BaseHelperBO consumerCategoryType : consumerCategoryTypesList) {

                if(!PJConstants.ConsumerType.BIKEPURCHASECUSTOMER.equals(consumerCategoryType.getCodeDbid()) || CommonConstants.CHAR_ONE.equals(arg1)) {

                    if(StringUtils.isBlankText(arg0) && arg2.equals(CommonConstants.CHAR_ZERO)) {

                        if(PJConstants.ConsumerType.POTENTIALCUSTOMER.getCodeDbid().equals(consumerCategoryType.getCodeDbid())
                           || PJConstants.ConsumerType.TWOSCUSTOMER.getCodeDbid().equals(consumerCategoryType.getCodeDbid())) {

                            consumerCategoryTypes.add(consumerCategoryType);
                        }
                    } else {

                        consumerCategoryTypes.add(consumerCategoryType);
                    }
                }
            }

            return consumerCategoryTypes;
        }

        return consumerCategoryTypesList;
    }

    public List<ConstantsBO> getContactTypeList() {

        return constantsLogic.getConstantsData(PJConstants.ContactMechanismType.class.getDeclaredFields());
    }

    public List<ConstantsBO> getConsumerTypeList() {

        return constantsLogic.getConstantsData(PJConstants.ConsumerType.class.getDeclaredFields());
    }

    public List<ConstantsBO> getCmmRemindTypeList() {

        return constantsLogic.getConstantsData(PJConstants.SalesLead.class.getDeclaredFields());
    }

    public List<ConstantsBO> getAdjustmentTypeList() {

        return constantsLogic.getConstantsData(PJConstants.StockAdjustmentType.class.getDeclaredFields());
    }

    public List<ConstantsBO> getPartsReturnRequestStatus() {

        return constantsLogic.getConstantsData(PJConstants.ReturnRequestStatus.class.getDeclaredFields());
    }

    public List<ConstantsBO> getPOMethodList() {

        return constantsLogic.getConstantsData(PJConstants.PurchaseMethodType.class.getDeclaredFields());
    }

    public List<ConstantsBO> getPriceTypeList() {

        return constantsLogic.getConstantsData(PJConstants.PriceCategory.class.getDeclaredFields());
    }

    public List<BaseHelperBO> getJudgementStausList(String siteId, String userId) {

        String type = "";

        if (YmvnSite.S075YMVNSITE.equals(siteId)) {
            SysUserAuthorityVO sysUserAuthorityVO = helperService.getUserAuthorityByUserId(userId);
            String roleList = sysUserAuthorityVO.getRoleList();
            JSONArray jsonArray = JSON.parseArray(roleList);
            List<String> roleCodeList = jsonArray.stream().map(obj -> (JSONObject) obj).map(obj -> obj.getString("roleCode")).toList();
            if (roleCodeList.contains(PJConstants.RoleCode.ACCOUNT) && !roleCodeList.contains(PJConstants.RoleCode.YMVNSD) && !roleCodeList.contains(PJConstants.RoleCode.SERIALCOUNTER)) {
                type = PJConstants.JudgementStatusType.ACCT;
            } else if (!roleCodeList.contains(PJConstants.RoleCode.ACCOUNT) && (roleCodeList.contains(PJConstants.RoleCode.YMVNSD) || roleCodeList.contains(PJConstants.RoleCode.SERIALCOUNTER))) {
                type = PJConstants.JudgementStatusType.YMVNSD;
            } else if (roleCodeList.contains(PJConstants.RoleCode.ACCOUNT) && (roleCodeList.contains(PJConstants.RoleCode.YMVNSD) || roleCodeList.contains(PJConstants.RoleCode.SERIALCOUNTER))) {
                type = PJConstants.JudgementStatusType.YMVNSDACCT;
            } else if (roleCodeList.contains(PJConstants.RoleCode.ACCOUNTINGPROMOTION)) {
                type = PJConstants.JudgementStatusType.ACCT;
            }
        } else {
            type = PJConstants.JudgementStatusType.DEALER;
        }

        return helperService.getJudgementStausList(type);
    }

    public List<ConstantsBO> getLocationClassificationList(String codeDbId1, String codeDbId2) {

        List<ConstantsBO> resultList = new ArrayList<>();

        List<ConstantsBO> locationClassificationList = constantsLogic.getConstantsData(PJConstants.LocationType.class.getDeclaredFields());

        if (StringUtils.isBlankText(codeDbId1) && StringUtils.isBlankText(codeDbId2)) {
            resultList = locationClassificationList;
        } else {
            for (ConstantsBO bo : locationClassificationList) {
                if (bo.getCodeDbid().equals(codeDbId1) || bo.getCodeDbid().equals(codeDbId2)) {
                    resultList.add(bo);
                }
            }
        }

        return resultList;
    }

    public List<ConstantsBO> getOrderCategoryTypeList(String codeId) {

        List<ConstantsBO> orderCategoryTypeList = new ArrayList<>();

        if (PJConstants.SalesOrderPriorityType.CODE_ID.equals(codeId)) {
            orderCategoryTypeList = constantsLogic.getConstantsData(PJConstants.SalesOrderPriorityType.class.getDeclaredFields());
        } else if (PJConstants.PurchaseOrderPriorityType.CODE_ID.equals(codeId)) {
            orderCategoryTypeList = constantsLogic.getConstantsData(PJConstants.PurchaseOrderPriorityType.class.getDeclaredFields());
        }

        return orderCategoryTypeList;
    }

    public List<MstProductCategoryVO> getModelTypeList() {

        List<MstProductCategoryVO> mstProductCateogryVOList = helperService.getMstProductCategoryByCategoryType(PJConstants.PartsCategory.SDSCATEGORY);
        List<MstProductCategoryVO> list = new ArrayList<>();

        for (MstProductCategoryVO vo : mstProductCateogryVOList) {
            if (PJConstants.CategoryCd.AT.equals(vo.getCategoryCd())
                    || PJConstants.CategoryCd.MP.equals(vo.getCategoryCd())
                    || PJConstants.CategoryCd.BIGBIKE.equals(vo.getCategoryCd())
                    || PJConstants.CategoryCd.EV.equals(vo.getCategoryCd())) {
                list.add(vo);
            }
        }

        return list;
    }

    public List<CascaderBO> getLargeGroupList() {

        List<MstProductCategoryVO> largeList = helperService.getMstProductCategoryByCategoryType(PJConstants.PartsCategory.LARGEGROUP);

        List<MstProductCategoryVO> middleList = helperService.getMstProductCategoryByCategoryType(PJConstants.PartsCategory.MIDDLEGROUP);

        List<CascaderBO> resultList = new ArrayList<>();

        for (MstProductCategoryVO largeVO : largeList) {
            CascaderBO cascaderBO = new CascaderBO();
            cascaderBO.setLabel(largeVO.getCategoryNm());
            cascaderBO.setValue(largeVO.getProductCategoryId());
            List<CascaderBO> childrenList = new ArrayList<>();
            for (MstProductCategoryVO middleVO : middleList) {
                if (middleVO.getParentCategoryId().equals(largeVO.getProductCategoryId())) {
                    CascaderBO childrenCascaderBO = new CascaderBO();
                    childrenCascaderBO.setLabel(middleVO.getCategoryNm());
                    childrenCascaderBO.setValue(middleVO.getProductCategoryId());
                    childrenList.add(childrenCascaderBO);
                }
            }
            cascaderBO.setChildren(childrenList);
            resultList.add(cascaderBO);
        }

        return resultList;
    }

    public List<MstProductCategoryVO> getSingleLargeGroupList() {

        return helperService.getMstProductCategoryByCategoryType(PJConstants.PartsCategory.LARGEGROUP);
    }

    public List<MstProductCategoryVO> getMiddleGroupList() {

        return helperService.getMstProductCategoryByCategoryType(PJConstants.PartsCategory.MIDDLEGROUP);
    }

    public List<ConstantsBO> getServiceStatusList() {

        return constantsLogic.getConstantsData(PJConstants.ServiceOrderStatus.class.getDeclaredFields());
    }

    public List<ConstantsBO> getStockAdjustmentReasonList() {

        return constantsLogic.getConstantsData(PJConstants.StockAdjustReasonType.class.getDeclaredFields());
    }

    public List<BaseHelperBO> getPointAddressList(String siteId) {

        return helperService.findByCodeIdAndSiteIdOrderByKey1(MstCodeConstants.FacilityMultiAddress.CODE_ID, siteId);
    }

    public List<BaseHelperBO> getPaymentMethodTypeList(String siteId) {

        if(CommonConstants.CHAR_SITE_ID_RY03.equals(siteId)) {

            return helperService.findByCodeIdAndSiteIdOrderByKey1(MstCodeConstants.PaymentMethodType.CODE_ID, CommonConstants.CHAR_SITE_ID_RY03);
        }else {

            return helperService.findByCodeIdAndSiteIdOrderByKey1(MstCodeConstants.PaymentMethodType.CODE_ID, CommonConstants.CHAR_DEFAULT_SITE_ID);
        }
    }

    public List<ConstantsBO> getContactStatusList() {

        return constantsLogic.getConstantsData(PJConstants.SalesLeadContactStatus.class.getDeclaredFields());
    }

    public List<ConstantsBO> getRelationshipList() {

        return constantsLogic.getConstantsData(PJConstants.Relationship.class.getDeclaredFields());
    }

    public List<ConstantsBO> getReturnRequestItemStatusList() {

        List<ConstantsBO> returnRequestItemStatusList = new ArrayList<>();
        List<ConstantsBO> returnRequestItemStatus = constantsLogic.getConstantsData(PJConstants.ReturnRequestStatus.class.getDeclaredFields());

        for(ConstantsBO Status : returnRequestItemStatus) {

            if(PJConstants.ReturnRequestStatus.APPROVED.getCodeDbid().equals(Status.getCodeDbid()) || PJConstants.ReturnRequestStatus.COMPLETED.getCodeDbid().equals(Status.getCodeDbid())) {

                returnRequestItemStatusList.add(Status);
            }
        }
        return returnRequestItemStatusList;
    }

    public List<ConstantsBO> getSalesReturnReasonList(String arg0) {

        List<ConstantsBO> salesReturnReasonList = new ArrayList<>();
        List<ConstantsBO> salesReturnReasons = constantsLogic.getConstantsData(PJConstants.ReturnReason.class.getDeclaredFields());

        for(ConstantsBO reason : salesReturnReasons) {

            if(StringUtils.equals(arg0, reason.getCodeData2()) && (StringUtils.equals(arg0, ProductClsType.GOODS.getCodeDbid()) || StringUtils.equals(arg0, ProductClsType.PART.getCodeDbid()))) {

                salesReturnReasonList.add(reason);
            }
        }

        return salesReturnReasonList;
    }

    public List<ConstantsBO> getTaxControlList() {

        return constantsLogic.getConstantsData(PJConstants.VatCode.class.getDeclaredFields());
    }

    public List<WorkzoneVO> getWorkZoneList(String siteId, String facilityId) {

        List<WorkzoneVO> workzoneList;

        // 当facilityId有值时，加上查询条件facilityId，否则查询siteId下所有的workzone
        if(StringUtils.isNotBlankText(facilityId)){
            workzoneList = helperService.getWorkZoneBySiteIdAndFacilityId(siteId, Long.valueOf(facilityId));
        }else{
            workzoneList = helperService.getWorkZoneBySiteId(siteId);
        }

        return workzoneList;
    }

    public List<ConstantsBO> getSvCategoryList(String arg0) {

        List<ConstantsBO> result = new ArrayList<>();

        if (StringUtils.isNotBlankText(arg0)) {
            if (PJConstants.ServiceCategory.CLAIM.getCodeDbid().equals(arg0)) {
                result.add(PJConstants.ServiceCategory.CLAIM);
                result.add(PJConstants.ServiceCategory.CLAIMBATTERY);
            } else if (PJConstants.ServiceCategory.CLAIMBATTERY.getCodeDbid().equals(arg0)) {
                result.add(PJConstants.ServiceCategory.CLAIMBATTERY);
            } else if (PJConstants.ServiceCategory.REPAIR.getCodeDbid().equals(arg0)) {
                result.add(PJConstants.ServiceCategory.REPAIR);
                result.add(PJConstants.ServiceCategory.FREESERVICE);
            }  else if (PJConstants.ServiceCategory.SPECIALCLAIM.getCodeDbid().equals(arg0)) {
                result.add(PJConstants.ServiceCategory.CLAIM);
                result.add(PJConstants.ServiceCategory.SPECIALCLAIM);
            } else {
                result.add(PJConstants.ServiceCategory.FREECOUPON);
                result.add(PJConstants.ServiceCategory.SPECIALCLAIM);
                result.add(PJConstants.ServiceCategory.CLAIMBATTERY);
                result.add(PJConstants.ServiceCategory.CLAIM);
            }
        } else {
            result.addAll(constantsLogic.getConstantsData(PJConstants.ServiceCategory.class.getDeclaredFields()).stream().filter(svCategory -> !StringUtils.equals(svCategory.getCodeDbid(), PJConstants.ServiceCategory.CLAIMBATTERY.getCodeDbid())).toList());
        }

        return result;
    }

    public List<ConstantsBO> getContactStatus2sList() {

        return constantsLogic.getConstantsData(PJConstants.ContactStatus2s.class.getDeclaredFields());
    }

    public List<ConstantsBO> getLeadStatus2sList() {

        return constantsLogic.getConstantsData(PJConstants.LeadStatus2s.class.getDeclaredFields());
    }

    public List<ConstantsBO> getRatioTypeList() {

        return constantsLogic.getConstantsData(PJConstants.RatioType.class.getDeclaredFields());
    }

    public List<ConstantsBO> getTraderRoleList() {

        return constantsLogic.getConstantsData(PJConstants.PartyRoleGroupType.class.getDeclaredFields());
    }

    public List<ConstantsBO> getRegistrationDocumentFeatrueCategoryList() {

        List<ConstantsBO> list = constantsLogic.getConstantsData(PJConstants.RegistrationDocumentFeatrueCategory.class.getDeclaredFields());

        List<ConstantsBO> result = new ArrayList<>();

        for (ConstantsBO bo : list) {
            if (PJConstants.RegistrationDocumentFeatrueCategory.OWNERTYPE.equals(bo.getCodeData2())) {
                result.add(bo);
            }
        }

        return result;
    }

    public List<ConstantsBO> getUseTypeList() {

        List<ConstantsBO> list = constantsLogic.getConstantsData(PJConstants.RegistrationDocumentFeatrueCategory.class.getDeclaredFields());

        List<ConstantsBO> result = new ArrayList<>();

        for (ConstantsBO bo : list) {
            if (PJConstants.RegistrationDocumentFeatrueCategory.USETYPE.equals(bo.getCodeData2())) {
                result.add(bo);
            }
        }

        return result;
    }

    public List<CmmGeorgaphyVO> getProvinceList() {

        return helperService.getCmmGeorgaphyByGeographyClassificationId(PJConstants.GeographyClassification.PROVINCE.getCodeDbid());
    }

    public List<CmmGeorgaphyVO> getCityList() {

        return helperService.getCmmGeorgaphyByGeographyClassificationId(PJConstants.GeographyClassification.CITY.getCodeDbid());
    }

    public List<CmmGeorgaphyVO> getCityByProvince(String provinceId) {

        return helperService.getCityByProvince(PJConstants.GeographyClassification.CITY.getCodeDbid(), Long.valueOf(provinceId));
    }

    public List<ConstantsBO> getNumberPatternList() {

        return constantsLogic.getConstantsData(PJConstants.NumberPattern.class.getDeclaredFields());
    }

    public List<ConstantsBO> getPrintingPagerList() {

        return constantsLogic.getConstantsData(PJConstants.PrintingPager.class.getDeclaredFields());
    }

    public List<ConstantsBO> getCostUsageList() {

        return constantsLogic.getConstantsData(PJConstants.CostUsage.class.getDeclaredFields());
    }

    public List<ConstantsBO> getCmProcessDefinitionList() {

        return constantsLogic.getConstantsData(PJConstants.OrderType.class.getDeclaredFields());
    }

    public List<ConstantsBO> getCmProcessTaskTypeList() {
        List<ConstantsBO> resultList = new ArrayList<>();

        List<ConstantsBO> salesOrderStatusList = getMstCodeData(SalesOrderStatus.CODE_ID);

        List<ConstantsBO> serviceOrderStatusList = constantsLogic.getConstantsData(PJConstants.ServiceOrderStatus.class.getDeclaredFields());

        List<ConstantsBO> combinedList = new ArrayList<>();
        combinedList.addAll(salesOrderStatusList);
        combinedList.addAll(serviceOrderStatusList);

        combinedList.stream().filter(item -> StringUtils.equals(FinishStatus.UN_FINISHED.getCodeDbid(),item.getCodeData3())).forEach(resultList::add);

        return resultList;
    }

    public List<ConstantsBO> getOrderStatus(String codeId, String codeData2) {

        List<ConstantsBO> resultList = new ArrayList<>();

        if (SalesOrderStatus.CODE_ID.equals(codeId)) {
        	List<ConstantsBO> salesOrderStatusList = getMstCodeData(SalesOrderStatus.CODE_ID);
            resultList = salesOrderStatusList.stream().filter(bo -> bo.getCodeData2().equals(codeData2)).collect(Collectors.toList());
        } else if (PJConstants.ServiceOrderStatus.CODE_ID.equals(codeId)) {
            resultList.addAll(constantsLogic.getConstantsData(PJConstants.ServiceOrderStatus.class.getDeclaredFields()));
        }

        return resultList;
    }

    private List<ConstantsBO> getMstCodeData(String codeId) {
    	List<ConstantsBO> salesOrderStatusList = new ArrayList<>();
        // SalesOrderStatus: S015
        List<BaseHelperBO> mstCodeInfos = helperService.getMstCodeInfoByCodeId(codeId);
        for(BaseHelperBO item : mstCodeInfos) {
        	ConstantsBO constantBO = BeanMapUtils.mapTo(item, ConstantsBO.class);
        	salesOrderStatusList.add(constantBO);
        }

        return salesOrderStatusList;
    }
    public List<ConstantsBO> getGenderTypeList() {

        return constantsLogic.getConstantsData(PJConstants.GenderType.class.getDeclaredFields());
    }

    public List<MstProductCategoryVO> getProductCategoryList(String productClassificationId, String siteId) {

        return helperService.getProductCategoryList(productClassificationId, siteId);
    }

    public List<ConstantsBO> getServicePaymentStatusList() {

        return constantsLogic.getConstantsData(PJConstants.ServicePaymentStatus.class.getDeclaredFields());
    }

    public List<ConstantsBO> getDemandSourceList() {

        List<ConstantsBO> resultList = new ArrayList<>();
        ConstantsBO orginalBo = new ConstantsBO();
        ConstantsBO currentBo = new ConstantsBO();

        orginalBo.setCodeData1(PJConstants.DemandSource.ORIGINALDEMAND);
        currentBo.setCodeData1(PJConstants.DemandSource.CURRENTDEMAND);

        resultList.add(orginalBo);
        resultList.add(currentBo);

        return resultList;
    }

    public List<MstBrandVO> getBrandForDifferentFlagList(String agr0){

        List<MstBrandVO> brands = new ArrayList<>();
        List<MstBrandVO> brandList = helperService.getDefaultFlagIsequalsYBrandList(CommonConstants.CHAR_DEFAULT_SITE_ID, CommonConstants.CHAR_SUPPER_ADMIN_FLAG);
        if(StringUtils.isNotBlankText(agr0)) {

            for(MstBrandVO brand : brandList) {

                if(CommonConstants.CHAR_ZERO.equals(agr0) && !PJConstants.BrandType.YAMAHA.getCodeData1().equals(brand.getBrandNm()) && !PJConstants.BrandType.NOBIKE.getCodeData1().equals(brand.getBrandNm())) {

                    brands.add(brand);
                }else if(CommonConstants.CHAR_ONE.equals(agr0) && !PJConstants.BrandType.KIMOCO.getCodeData1().equals(brand.getBrandNm())) {

                    brands.add(brand);
                }else if(CommonConstants.CHAR_TWO.equals(agr0) && !PJConstants.BrandType.NOBIKE.getCodeData1().equals(brand.getBrandNm())) {

                    brands.add(brand);
                }
            }

            return brands;
        }

        return brandList;
    }

    public List<ConstantsBO> getUnavailableType() {

        return constantsLogic.getConstantsData(PJConstants.SpSalesStatusType.class.getDeclaredFields());
    }

    public List<ConstantsBO> getOrderSourceType() {

        List<String> targetCodes = List.of(ProductClsType.PART.getCodeDbid(), ProductClsType.SERVICE.getCodeDbid());
        List<ConstantsBO> productList = constantsLogic.getConstantsData(ProductClsType.class.getDeclaredFields());

        List<ConstantsBO> filteredList = productList.stream()
                .filter(product -> targetCodes.contains(product.getCodeDbid()))
                .collect(Collectors.toList());

        return filteredList;
    }

    /**
     * @author mid1439
     */
    public List<ConstantsBO> getManifestStatusType() {

        return constantsLogic.getConstantsData(PJConstants.ManifestStatus.class.getDeclaredFields());
    }

    /**
     * @author mid2259
     */
    public List<ConstantsBO> getManifestStatusTypeForSD() {

        return constantsLogic.getConstantsData(MstCodeConstants.ManifestStatus.class.getDeclaredFields());
    }

    /**
     * @author mid1439
     */
    public List<ConstantsBO> getReceiptSlipStatus(String codeId) {

        List<ConstantsBO> receiptSlipStatusList = constantsLogic.getConstantsData(PJConstants.ReceiptSlipStatus.class.getDeclaredFields());

        if (StringUtils.isNotEmpty(codeId)) {

            String[] codeStrArry = codeId.split(CommonConstants.CHAR_SLASH);

            return receiptSlipStatusList.stream().filter(element -> Arrays.stream(codeStrArry).anyMatch(element.getCodeDbid()::equals)).toList();
        }else {

            return receiptSlipStatusList;
        }
    }

    /**
     * @author mid1996
     */
    public List<BaseHelperBO> getScheduleTimeList(String siteId, String arg0, String arg1) {

        return helperService.getScheduleTimeList(siteId, arg0, arg1);
    }

    /**
     * @author mid2330
     */
    public List<ConstantsBO> getEffectStatus() {

        return constantsLogic.getConstantsData(PJConstants.EffectStatus.class.getDeclaredFields());
    }

    /**
     * @author mid2330
     */
    public List<ConstantsBO> getCustomerAndSupplier() {

        List<ConstantsBO> roleList = constantsLogic.getConstantsData(PJConstants.OrgRelationType.class.getDeclaredFields());
        Iterator<ConstantsBO> iterator = roleList.iterator();
        while (iterator.hasNext()) {
            ConstantsBO role = iterator.next();
            if (!PJConstants.OrgRelationType.SUPPLIER.getCodeDbid().equals(role.getCodeDbid())
                    && !PJConstants.OrgRelationType.CONSUMER.getCodeDbid().equals(role.getCodeDbid())) {
                iterator.remove();
            }
        }
        return roleList;
    }

    public List<MstCodeInfoVO> findByCodeIdAndCodeData2(String codeId, String codeData2){

        return helperService.findByCodeIdAndCodeData2(codeId, codeData2);
    }

    public List<BaseHelperBO> getMstCodeInfoByCodeId(String codeId){

        return helperService.getMstCodeInfoByCodeId(codeId);
    }

    public List<CmmHelperBO> getDealerList(List<String> organizationCdList) {

        List<CmmHelperBO> dealerList = new ArrayList<>();

        List<CmmMstOrganizationVO> resultVO = helperService.getDealerList(organizationCdList);

        for (CmmMstOrganizationVO cmmMstOrganizationVO : resultVO) {
            CmmHelperBO dealer = new CmmHelperBO();
            dealer.setId(cmmMstOrganizationVO.getOrganizationId());
            dealer.setCode(cmmMstOrganizationVO.getOrganizationCd());
            dealer.setName(cmmMstOrganizationVO.getOrganizationNm());
            dealer.setDesc(cmmMstOrganizationVO.getOrganizationCd() + CommonConstants.CHAR_SPACE + cmmMstOrganizationVO.getOrganizationNm());

            dealerList.add(dealer);
        }

        return dealerList;
    }

    public List<CmmHelperBO> getToPointList(String dealerCd) {

        List<CmmHelperBO> toPointList = new ArrayList<>();

        if (StringUtils.isNotEmpty(dealerCd)) {

            List<MstFacilityVO> resultVO = helperService.getToPointList(dealerCd);

            for (MstFacilityVO mstFacilityVO : resultVO) {

                CmmHelperBO toPoint = new CmmHelperBO();

                toPoint.setId(mstFacilityVO.getFacilityId());
                toPoint.setCode(mstFacilityVO.getFacilityCd());
                toPoint.setName(mstFacilityVO.getFacilityNm());
                toPoint.setDesc(mstFacilityVO.getFacilityCd() + CommonConstants.CHAR_SPACE + mstFacilityVO.getFacilityNm());

                toPointList.add(toPoint);
            }
        }

        return toPointList;
    }
}