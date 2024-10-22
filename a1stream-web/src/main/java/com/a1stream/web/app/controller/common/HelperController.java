package com.a1stream.web.app.controller.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.model.BaseHelperBO;
import com.a1stream.common.model.BaseHelperForm;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.CascaderBO;
import com.a1stream.common.model.CmmHelperBO;
import com.a1stream.common.model.ConstantsBO;
import com.a1stream.common.model.VueSelectVO;
import com.a1stream.domain.bo.common.RoleBO;
import com.a1stream.domain.vo.BinTypeVO;
import com.a1stream.domain.vo.CmmGeorgaphyVO;
import com.a1stream.domain.vo.MstBrandVO;
import com.a1stream.domain.vo.MstCodeInfoVO;
import com.a1stream.domain.vo.MstProductCategoryVO;
import com.a1stream.domain.vo.WorkzoneVO;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("common/helper")
public class HelperController implements RestProcessAware {

    @Resource
    private HelperFacade helperFacade;

    @PostMapping(value = "/getMstCodeInfos.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<BaseHelperBO>> getMstCodeInfos(@RequestBody final Set<String> codeIds) {

        return helperFacade.getMstCodeMapByCodeId(codeIds);
    }

    @PostMapping(value = "/getBinTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BinTypeVO> getBinType(@AuthenticationPrincipal final PJUserDetails uc) {

        return helperFacade.getBinTypeBySiteId(uc.getDealerCode());
    }

    @PostMapping(value = "/employeeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CmmHelperBO> employeeList(@RequestBody final BaseVLForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        return helperFacade.employeeList(model, uc.getDealerCode());
    }

    @PostMapping(value = "/pointList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CmmHelperBO> pointList(@RequestBody final BaseVLForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        return helperFacade.pointList(uc, model);
    }

    @PostMapping(value = "/supplierList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CmmHelperBO> supplierList(@RequestBody final BaseVLForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        return helperFacade.supplierList(uc.getDealerCode(), model);
    }

    @PostMapping(value = "/userRoleList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RoleBO> userRoleList(@RequestBody final BaseVLForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        return helperFacade.userRoleList(uc.getDealerCode(), model);
    }

    @PostMapping(value = "/getInventoryTransactionTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getInventoryTransactionType(@RequestBody final BaseHelperForm model) {

        return helperFacade.getInventoryTransactionTypeByArg(model.getArg0());
    }

    @PostMapping(value = "/getBrandListVL.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VueSelectVO> getBrandListVL() {

        List<MstBrandVO> resultsBrandVOs = helperFacade.getBrandList("");

        List<VueSelectVO> results = new ArrayList<>();
        for(MstBrandVO item : resultsBrandVOs ) {
            VueSelectVO model = new VueSelectVO();

            model.setLabel(item.getBrandNm());
            model.setValue(item.getBrandId());

            results.add(model);
        }

        return results;
    }

    @PostMapping(value = "/getBrandList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MstBrandVO> getBrandList(@RequestBody final BaseHelperForm model) {

        return helperFacade.getBrandList(model.getArg0());
    }

    @PostMapping(value = "/getConsumerCategoryTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BaseHelperBO> getConsumerCategoryTypeList(@RequestBody final BaseHelperForm model) {

        return helperFacade.getConsumerCategoryTypeList(model.getArg0(), model.getArg1(), model.getArg2());
    }

    @PostMapping(value = "/getContactTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getContactTypeList() {

        return helperFacade.getContactTypeList();
    }

    @PostMapping(value = "/getConsumerTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getConsumerTypeList() {

        return helperFacade.getConsumerTypeList();
    }

    @PostMapping(value = "/getCmmRemindTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getCmmRemindTypeList() {

        return helperFacade.getCmmRemindTypeList();
    }

    @PostMapping(value = "/getAdjustmentTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getAdjustmentTypeList() {

        return helperFacade.getAdjustmentTypeList();
    }

    @PostMapping(value = "/getPartsReturnRequestStatus.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getPartsReturnRequestStatus() {

        return helperFacade.getPartsReturnRequestStatus();
    }

    @PostMapping(value = "/getPOMethodList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getPOMethodList() {

        return helperFacade.getPOMethodList();
    }

    @PostMapping(value = "/getPriceTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getPriceTypeList() {

        return helperFacade.getPriceTypeList();
    }

    @PostMapping(value = "/getJudgementStausList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BaseHelperBO> getJudgementStausList(@RequestBody final BaseHelperForm model) {

        return helperFacade.getJudgementStausList(model.getArg0(), model.getArg1());
    }

    @PostMapping(value = "/getLocationClassificationList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getLocationClassificationList(@RequestBody final BaseHelperForm model) {

        return helperFacade.getLocationClassificationList(model.getArg0(), model.getArg1());
    }

    @PostMapping(value = "/getOrderCategoryTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getOrderCategoryTypeList(@RequestBody final BaseHelperForm model) {

        return helperFacade.getOrderCategoryTypeList(model.getArg0());
    }

    @PostMapping(value = "/getModelTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MstProductCategoryVO> getModelTypeList() {

        return helperFacade.getModelTypeList();
    }

    @PostMapping(value = "/getLargeGroupList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CascaderBO> getLargeGroupList() {

        return helperFacade.getLargeGroupList();
    }

    @PostMapping(value = "/getSingleLargeGroupList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MstProductCategoryVO> getSingleLargeGroupList() {

        return helperFacade.getSingleLargeGroupList();
    }

    @PostMapping(value = "/getMiddleGroupList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MstProductCategoryVO> getMiddleGroupList() {

        return helperFacade.getMiddleGroupList();
    }

    @PostMapping(value = "/getServiceStatusList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getServiceStatusList() {

        return helperFacade.getServiceStatusList();
    }

    @PostMapping(value = "/getStockAdjustmentReasonList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getStockAdjustmentReasonList() {

        return helperFacade.getStockAdjustmentReasonList();
    }

    @PostMapping(value = "/getPointAddressList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BaseHelperBO> getPointAddressList(@AuthenticationPrincipal final PJUserDetails uc) {

        return helperFacade.getPointAddressList(uc.getDealerCode());
    }

    @PostMapping(value = "/getPaymentMethodTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BaseHelperBO> getPaymentMethodTypeList(@AuthenticationPrincipal final PJUserDetails uc) {

        return helperFacade.getPaymentMethodTypeList(uc.getDealerCode());
    }

    @PostMapping(value = "/getContactStatusList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getContactStatusList() {

        return helperFacade.getContactStatusList();
    }

    @PostMapping(value = "/getRelationshipList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getRelationshipList() {

        return helperFacade.getRelationshipList();
    }

    @PostMapping(value = "/getReturnRequestItemStatusList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getReturnRequestItemStatusList() {

        return helperFacade.getReturnRequestItemStatusList();
    }

    @PostMapping(value = "/getSalesReturnReasonList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getSalesReturnReasonList(@RequestBody final BaseHelperForm model) {

        return helperFacade.getSalesReturnReasonList(model.getArg0());
    }

    @PostMapping(value = "/getTaxControlList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getTaxControlList() {

        return helperFacade.getTaxControlList();
    }

    @PostMapping(value = "/getWorkZoneList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WorkzoneVO> getWorkZoneList(@AuthenticationPrincipal final PJUserDetails uc,@RequestBody final BaseHelperForm model) {

        return helperFacade.getWorkZoneList(uc.getDealerCode(),model.getArg0());
    }

    @PostMapping(value = "/getSvCategoryList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getSvCategoryList(@RequestBody final BaseHelperForm model) {

        return helperFacade.getSvCategoryList(model.getArg0());
    }

    @PostMapping(value = "/getContactStatus2sList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getContactStatus2sList() {

        return helperFacade.getContactStatus2sList();
    }

    @PostMapping(value = "/getLeadStatus2sList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getLeadStatus2sList() {

        return helperFacade.getLeadStatus2sList();
    }

    @PostMapping(value = "/getRatioTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getRatioTypeList() {

        return helperFacade.getRatioTypeList();
    }

    @PostMapping(value = "/getTraderRoleList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getTraderRoleList() {

        return helperFacade.getTraderRoleList();
    }

    @PostMapping(value = "/getRegistrationDocumentFeatrueCategoryList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getRegistrationDocumentFeatrueCategoryList() {

        return helperFacade.getRegistrationDocumentFeatrueCategoryList();
    }

    @PostMapping(value = "/getUseTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getUseTypeList() {

        return helperFacade.getUseTypeList();
    }

    @PostMapping(value = "/getProvinceList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CmmGeorgaphyVO> getProvinceList() {

        return helperFacade.getProvinceList();
    }

    @PostMapping(value = "/getCityList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CmmGeorgaphyVO> getCityList() {

        return helperFacade.getCityList();
    }

    @PostMapping(value = "/getCityByProvince.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CmmGeorgaphyVO> getCityByProvince(@RequestBody final BaseHelperForm model) {

        return helperFacade.getCityByProvince(model.getArg0());
    }

    @PostMapping(value = "/getNumberPatternList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getNumberPatternList() {

        return helperFacade.getNumberPatternList();
    }

    @PostMapping(value = "/getPrintingPagerList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getPrintingPagerList() {

        return helperFacade.getPrintingPagerList();
    }

    @PostMapping(value = "/getCostUsageList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getCostUsageList() {

        return helperFacade.getCostUsageList();
    }

    @PostMapping(value = "/getCmProcessDefinitionList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getCmProcessDefinitionList() {

        return helperFacade.getCmProcessDefinitionList();
    }

    @PostMapping(value = "/getCmProcessTaskTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getCmProcessTaskTypeList() {

        return helperFacade.getCmProcessTaskTypeList();
    }

    @PostMapping(value = "/getOrderStatus.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getOrderStatus(@RequestBody final BaseHelperForm model) {

        return helperFacade.getOrderStatus(model.getArg0(), model.getArg1());
    }

    @PostMapping(value = "/getGenderTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getGenderTypeList() {

        return helperFacade.getGenderTypeList();
    }

    @PostMapping(value = "/getProductCategoryList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MstProductCategoryVO> getProductCategoryList(@RequestBody final BaseHelperForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        return helperFacade.getProductCategoryList(model.getArg0(), uc.getDealerCode());
    }

    @PostMapping(value = "/getServicePaymentStatusList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getServicePaymentStatusList() {

        return helperFacade.getServicePaymentStatusList();
    }

    @PostMapping(value = "/getDemandSourceList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getDemandSourceList() {

        return helperFacade.getDemandSourceList();
    }

    //实现在一定条件下去除defaultFlag=Y的查询
    @PostMapping(value = "/getBrandForDifferentFlagList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MstBrandVO> getBrandForDifferentFlagList(@RequestBody final BaseHelperForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        if(CommonConstants.CHAR_DEFAULT_SITE_ID.equals(uc.getDealerCode())) {
            return helperFacade.getBrandList(model.getArg0());
        }else {
            return helperFacade.getBrandForDifferentFlagList(model.getArg0());
        }
    }

    @PostMapping(value = "/getUnavailable.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getUnavailableType() {

        return helperFacade.getUnavailableType();
    }

    @PostMapping(value = "/getOrderSourceType.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getOrderSourceType() {

        return helperFacade.getOrderSourceType();
    }

    @PostMapping(value = "/getManifestStatusType.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getManifestStatusType() {

        return helperFacade.getManifestStatusType();
    }

    @PostMapping(value = "/getManifestStatusTypeForSD.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getManifestStatusTypeForSD() {

        return helperFacade.getManifestStatusTypeForSD();
    }

    @PostMapping(value = "/getReceiptSlipStatus.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getReceiptSlipStatus(@RequestBody final BaseHelperForm model) {

        return helperFacade.getReceiptSlipStatus(model.getArg0());
    }

    @PostMapping(value = "/getReservationTime.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BaseHelperBO> getReservationTime(@RequestBody final BaseHelperForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        return helperFacade.getScheduleTimeList(uc.getDealerCode(), model.getArg0(), model.getArg1());
    }

    @PostMapping(value = "/getEffectStatus.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getEffectStatus() {

        return helperFacade.getEffectStatus();
    }

    @PostMapping(value = "/getRoleTypeCustomerAndSupplier.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConstantsBO> getCustomerAndSupplier() {

        return helperFacade.getCustomerAndSupplier();
    }

    @PostMapping(value = "/getMstCodeInfoList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MstCodeInfoVO> findByCodeIdAndCodeData2(@RequestBody final BaseHelperForm model) {

        return helperFacade.findByCodeIdAndCodeData2(model.getArg0(), model.getArg1());
    }

    @PostMapping(value = "/getMstCodeInfoByCodeId.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BaseHelperBO> getMstCodeInfoByCodeId(@RequestBody final BaseHelperForm model) {

        return helperFacade.getMstCodeInfoByCodeId(model.getCodeId());
    }

    @PostMapping(value = "/getDealerList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CmmHelperBO> getDealerList(@RequestBody final BaseHelperForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        List<String> excludeOrgCdList = Arrays.asList("666N", "CONSUMER", uc.getDealerCode());
        return helperFacade.getDealerList(excludeOrgCdList);
    }

    @PostMapping(value = "/getToPointList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CmmHelperBO> getToPointList(@RequestBody final BaseHelperForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        return helperFacade.getToPointList(model.getArg0());
    }
}