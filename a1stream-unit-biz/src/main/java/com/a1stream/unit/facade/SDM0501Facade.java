package com.a1stream.unit.facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.McSalesStatus;
import com.a1stream.domain.bo.unit.SDM050101BO;
import com.a1stream.domain.bo.unit.SDM050102BO;
import com.a1stream.domain.form.unit.SDM050101Form;
import com.a1stream.domain.form.unit.SDM050102Form;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.CmmUnitPromotionItemVO;
import com.a1stream.domain.vo.CmmUnitPromotionListVO;
import com.a1stream.domain.vo.CmmUnitPromotionModelVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.a1stream.unit.service.SDM0501Service;
import com.alibaba.excel.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

@Component
public class SDM0501Facade {

    @Resource
    private SDM0501Service sdm0501Service;

    public List<SDM050101BO> findSetUpPromotionList(SDM050101Form form, PJUserDetails uc) {

        //查询校验
        this.check(form);

        //判断是否为666N的 SD或财务
        SysUserAuthorityVO sysUserAuthorityVO =  sdm0501Service.findSysUserAuthority(uc.getUserId());

        if(sysUserAuthorityVO!=null) {
            form.setSdOrAccountFlag(CommonConstants.CHAR_TRUE);
        }

        return sdm0501Service.findSetUpPromotionList(form);
    }

    private void check(SDM050101Form form) {

        //校验period时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

        LocalDate dateFrom = LocalDate.parse(form.getDateFrom(), formatter);
        LocalDate dateTo = LocalDate.parse(form.getDateTo(), formatter);

        if (dateFrom.isAfter(dateTo)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("error.dateEqAfter", new String[] {
                                             CodedMessageUtils.getMessage("label.toDate"),
                                             CodedMessageUtils.getMessage("label.fromDate")}));
        }

        if (dateFrom.plusMonths(CommonConstants.INTEGER_TWELVE).isBefore(dateTo)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10296", new String[] {
                                             CodedMessageUtils.getMessage("label.fromDate"),
                                             CodedMessageUtils.getMessage("label.toDate")}));
        }

        //校验promotion
        if(StringUtils.isNotBlank(form.getPromotion()) && ObjectUtils.isEmpty(form.getPromotionId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.promotion"),
                                             form.getPromotion(),
                                             CodedMessageUtils.getMessage("label.tablePromotionList")}));
        }

        //校验model
        if(StringUtils.isNotBlank(form.getModel()) && ObjectUtils.isEmpty(form.getModelId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.model"),
                                             form.getModel(),
                                             CodedMessageUtils.getMessage("label.productInformation")}));
        }
    }

    public List<SDM050101BO> findSetUpPromotionListExport(SDM050101Form form, PJUserDetails uc) {

        SysUserAuthorityVO sysUserAuthorityVO =  sdm0501Service.findSysUserAuthority(uc.getUserId());

        if(sysUserAuthorityVO!=null) {
            form.setSdOrAccountFlag(CommonConstants.CHAR_TRUE);
        }

        //不需要转化日期格式为DD/MM/YYYY,后续有修改请确认
        return sdm0501Service.findSetUpPromotionListExport(form);
    }

    public SDM050101BO findDetailAndAddFlag(PJUserDetails uc) {

        return sdm0501Service.findDetailAndAddFlag(uc.getUserId());
    }

    public List<SDM050102BO> findPromotionMC(SDM050102Form form, PJUserDetails uc) {
        //目前SDM050102查询导出共用该方法 不用格式化且无日期导出
        //查询校验
        this.checkPromotionMC(form);

        //判断是否为666N的 SD或财务
        SysUserAuthorityVO sysUserAuthorityVO =  sdm0501Service.findSysUserAuthority(uc.getUserId());

        if(sysUserAuthorityVO!=null) {
            form.setSdOrAccountFlag(CommonConstants.CHAR_TRUE);
        }

        return sdm0501Service.findPromotionMC(form);
    }

    private void checkPromotionMC(SDM050102Form form) {

        //校验dealer
        if(StringUtils.isNotBlank(form.getDealer()) && ObjectUtils.isEmpty(form.getDealerId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.dealer"),
                                             form.getDealer(),
                                             CodedMessageUtils.getMessage("label.tableCustomerInfo")}));
        }

        //校验model
        if(StringUtils.isNotBlank(form.getModel()) && ObjectUtils.isEmpty(form.getModelId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.model"),
                                             form.getModel(),
                                             CodedMessageUtils.getMessage("label.productInformation")}));
        }
    }

    public SDM050102Form checkFile(SDM050102Form form, String siteId) {

        // 将导入的数据和error信息设置到画面上
        return setSreenList(form, siteId);
    }

    private SDM050102Form setSreenList(SDM050102Form form, String siteId) {

        List<SDM050102BO> importList = form.getImportList();

        if (CollectionUtils.isEmpty(importList)) {
            return form;
        }

        //获取唯一一条Promotion信息
        CmmUnitPromotionListVO cmmUnitPromotionListVO = sdm0501Service.findByPromotionListId(Long.valueOf(form.getOtherProperty().toString()));

        // 转大写 去除前后空格
        for (SDM050102BO formatBo : importList) {
            formatBo.setDealerCd(formatStr(formatBo.getDealerCd()));
            formatBo.setPointCd(formatStr(formatBo.getPointCd()));
            formatBo.setModelCd(formatStr(formatBo.getModelCd()));
            formatBo.setFrameNo(formatStr(formatBo.getFrameNo()));
        }

        Set<String> seen = new HashSet<>();
        //dealer 直接用dealercd对应表sitecd查询
        Map<String, CmmSiteMasterVO> dealerMap = getDealerMap(importList);
        //point
        Map<String, MstFacilityVO> facilityMap = getFacilityMap(siteId, importList);
        //model
        Map<String, MstProductVO> productMap = getProductMap(siteId, importList);
        //frameNo
        Map<String, CmmSerializedProductVO> frameNoMap = getFrameNoMap(importList);

        List<CmmUnitPromotionModelVO> cmmUnitPromotionModelVOs = sdm0501Service.findFrameNoByPromotionListId(Long.valueOf(form.getOtherProperty().toString()));
        List<String> productCds = cmmUnitPromotionModelVOs.stream()
                                 .map(CmmUnitPromotionModelVO::getProductCd)
                                 .toList();

        for (SDM050102BO bo : importList) {

           // 设置error信息
            setErrorInfo(siteId
                       , seen
                       , dealerMap
                       , facilityMap
                       , productMap
                       , frameNoMap
                       , productCds
                       , bo);

            if(productMap.containsKey(bo.getModelCd())) {
                bo.setModelCd(productMap.get(bo.getModelCd()).getProductCd());
                bo.setModelNm(productMap.get(bo.getModelCd()).getSalesDescription());
                bo.setModelId(productMap.get(bo.getModelCd()).getProductId());
            }

            if(dealerMap.containsKey(bo.getDealerCd())) {
                bo.setDealerCd(dealerMap.get(bo.getDealerCd()).getSiteCd());
                bo.setDealerNm(dealerMap.get(bo.getDealerCd()).getSiteNm());
                bo.setDealerId(dealerMap.get(bo.getDealerCd()).getSiteId());
            }

            if(facilityMap.containsKey(bo.getPointCd())) {
                bo.setProvince(facilityMap.get(bo.getPointCd()).getProvinceNm());
                bo.setPointNm(facilityMap.get(bo.getPointCd()).getFacilityNm());
                bo.setPointId(facilityMap.get(bo.getPointCd()).getFacilityId());
                bo.setPointCd(facilityMap.get(bo.getPointCd()).getFacilityCd());
            }
            //增加PromotionCd和PromotionNm列数据
            bo.setPromoCd(cmmUnitPromotionListVO.getPromotionCd());
            bo.setPromoNm(cmmUnitPromotionListVO.getPromotionNm());
            //增加province列信息
        }
        return form;
    }

    private String formatStr(String code) {
        return StringUtils.isNotBlank(code) ? code.trim().toUpperCase() : CommonConstants.CHAR_BLANK;
    }

    private Map<String, CmmSiteMasterVO> getDealerMap(List<SDM050102BO> detaiList) {

        Map<String, CmmSiteMasterVO> vomap = new HashMap<>();
        Set<String> dealerCds = detaiList.stream().map(SDM050102BO::getDealerCd).collect(Collectors.toSet());
        if(!dealerCds.isEmpty()) {
            vomap = sdm0501Service.getDealerByCd(dealerCds).stream().collect(Collectors.toMap(v -> v.getSiteCd(),Function.identity()));
        }
        return vomap;
    }

    private Map<String, MstFacilityVO> getFacilityMap(String siteId, List<SDM050102BO> detaiList) {

        Map<String, MstFacilityVO> vomap = new HashMap<>();
        Set<String> facilityCds = detaiList.stream().map(SDM050102BO::getPointCd).collect(Collectors.toSet());
        if(!facilityCds.isEmpty()) {
            vomap = sdm0501Service.getFacilityByCd(facilityCds, siteId).stream().collect(Collectors.toMap(v -> v.getFacilityCd(),Function.identity()));
        }
        return vomap;
    }

    private Map<String, MstProductVO> getProductMap(String siteId, List<SDM050102BO> detaiList) {

        Map<String, MstProductVO> vomap = new HashMap<>();
        List<String> siteList = List.of(CommonConstants.CHAR_DEFAULT_SITE_ID, siteId);
        Set<String> productCds = detaiList.stream().map(SDM050102BO::getModelCd).collect(Collectors.toSet());

        if(!siteList.isEmpty() && !productCds.isEmpty()) {
            vomap = sdm0501Service.getProductByCds(productCds, siteList).stream().collect(Collectors.toMap(v -> v.getProductCd(),Function.identity()));
        }
        return vomap;
    }

    private Map<String, CmmSerializedProductVO> getFrameNoMap(List<SDM050102BO> detaiList) {

        Map<String, CmmSerializedProductVO> vomap = new HashMap<>();
        Set<String> frameNos = detaiList.stream().map(SDM050102BO::getFrameNo).collect(Collectors.toSet());

        if(!frameNos.isEmpty()) {
            vomap = sdm0501Service.findByFrameNos(frameNos).stream().collect(Collectors.toMap(v -> v.getFrameNo(),Function.identity()));
        }
        return vomap;
    }

    private void setErrorInfo(String siteId
            , Set<String> seen
            , Map<String, CmmSiteMasterVO> dealerMap
            , Map<String, MstFacilityVO> facilityMap
            , Map<String, MstProductVO> productMap
            , Map<String, CmmSerializedProductVO> frameNoMap
            , List<String> productCds
            , SDM050102BO bo) {

        StringBuilder errorMsg = new StringBuilder();
        List<String> error = new ArrayList<>();

        // check1: 如果Dealer Code为空或者Dealer Code不存在
        if(StringUtils.isBlank(bo.getDealerCd()) || !dealerMap.containsKey(bo.getDealerCd())) {

            errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                                            , new Object[]{CodedMessageUtils.getMessage("label.dealerCode")
                                            , bo.getDealerCd()
                                            , CodedMessageUtils.getMessage("label.tableCmmSiteMst")}));
        }

        // check2: 如果Point Code为空或者Point Code不存在
        if(StringUtils.isBlank(bo.getPointCd()) || !facilityMap.containsKey(bo.getPointCd())) {

            errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                                            , new Object[]{CodedMessageUtils.getMessage("label.pointCode")
                                            , bo.getPointCd()
                                            , CodedMessageUtils.getMessage("label.tableFacilityInfo")}));
        }

        // check3: 如果Model Code为空或者Model Code不存在
        if(StringUtils.isBlank(bo.getModelCd()) || !productMap.containsKey(bo.getModelCd())) {

            errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                                            , new Object[]{CodedMessageUtils.getMessage("label.modelCode")
                                            , bo.getModelCd()
                                            , CodedMessageUtils.getMessage("label.tableProduct")}));
        }

        // check4: 如果Frame No为空或者Frame No不存在
        if(StringUtils.isBlank(bo.getFrameNo()) || !frameNoMap.containsKey(bo.getFrameNo())) {

            errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                                            , new Object[]{CodedMessageUtils.getMessage("label.frameNumber")
                                            , bo.getFrameNo()
                                            , CodedMessageUtils.getMessage("common.label.vehicleInfo")}));
        }

        // check5: siteId 和 facilityId的匹配性校验
        if(!facilityMap.containsKey(bo.getPointCd()) || !facilityMap.get(bo.getPointCd()).getSiteId().equals(siteId)) {

            errorMsg.append(CodedMessageUtils.getMessage("M.E.00602"
                                            , new Object[]{CodedMessageUtils.getMessage("label.procDefSite")
                                            , CodedMessageUtils.getMessage("label.shop")}));
        }

        // check6: 上传车辆是否已卖出
        if(!frameNoMap.containsKey(bo.getFrameNo()) ||
                (StringUtils.isNotBlank(frameNoMap.get(bo.getFrameNo()).getSalesStatus()) && McSalesStatus.SALESTOUSER.equals(frameNoMap.get(bo.getFrameNo()).getSalesStatus()))) {

            errorMsg.append(CodedMessageUtils.getMessage("M.E.10301"
                                            , new Object[]{CodedMessageUtils.getMessage("label.frameNumber")
                                            , bo.getFrameNo()}));
        }

        // check7: 验证车型是否和当前该促销的车型一致
        if(!productCds.contains(bo.getModelCd())) {

            errorMsg.append(CodedMessageUtils.getMessage("M.E.10302"
                                            , new Object[]{CodedMessageUtils.getMessage("label.modelCode")
                                            , bo.getModelCd()}));
        }

        // check8: 导入数据是否重复
        String key = bo.getDealerCd() + "-" + bo.getPointCd()+ "-" + bo.getModelCd()+ "-" + bo.getFrameNo();
        if(!seen.add(key)) {

            errorMsg.append(CodedMessageUtils.getMessage("M.E.00306"
                                            , new Object[]{CodedMessageUtils.getMessage("label.dealerCode")
                                            , bo.getDealerCd()
                                            , CodedMessageUtils.getMessage("label.pointCode")
                                            , bo.getPointCd()
                                            , CodedMessageUtils.getMessage("label.modelNo")
                                            , bo.getModelCd()
                                            , CodedMessageUtils.getMessage("label.frameNumber")
                                            , bo.getFrameNo()}));
        }

        bo.setErrorMessage(errorMsg.toString());
        if (errorMsg != null && errorMsg.length() > 0) {
            error.add(errorMsg.toString());
        }
        bo.setError(error);
    }

    public void doConfirm(SDM050102Form form) {

        List<CmmUnitPromotionItemVO> cmmUnitPromotionItemVOList = new ArrayList<>();
        List<SDM050102BO> detaiList = form.getGridDataList();
        Map<String, CmmSerializedProductVO> frameNoMap = getFrameNoMap(detaiList);

        //新增表cmm_unit_promotion_item
        for(SDM050102BO bo : detaiList) {
            if(!StringUtils.isBlank(bo.getDealerId())) {
                CmmUnitPromotionItemVO cmmUnitPromotionItemVO = CmmUnitPromotionItemVO.create();
                cmmUnitPromotionItemVO.setPromotionListId(form.getPromotionListId());
                cmmUnitPromotionItemVO.setSiteId(bo.getDealerId());
                cmmUnitPromotionItemVO.setFacilityId(bo.getPointId());
                cmmUnitPromotionItemVO.setProductId(bo.getModelId());
                cmmUnitPromotionItemVO.setCmmSerializedProductId(frameNoMap.get(bo.getFrameNo()).getSerializedProductId());
                cmmUnitPromotionItemVO.setFrameNo(bo.getFrameNo());
                cmmUnitPromotionItemVO.setStockMcFlag(CommonConstants.CHAR_Y);
                cmmUnitPromotionItemVOList.add(cmmUnitPromotionItemVO);
            }
        }

        //更新表cmm_unit_promotion_list
        CmmUnitPromotionListVO cmmUnitPromotionListVO = sdm0501Service.findByPromotionListId(form.getPromotionListId());
        cmmUnitPromotionListVO.setModifyFlag(CommonConstants.CHAR_Y);

        sdm0501Service.doConfirm(cmmUnitPromotionItemVOList, cmmUnitPromotionListVO);
    }

    public void doDelete(SDM050102Form form) {

        Map<String, CmmSerializedProductVO> frameNoMap = getFrameNoMap(form.getGridDataList());

        // 删除前验证
        for(SDM050102BO bo : form.getGridDataList()) {
            if(StringUtils.isNotBlank(frameNoMap.get(bo.getFrameNo()).getSalesStatus()) && McSalesStatus.SALESTOUSER.equals(frameNoMap.get(bo.getFrameNo()).getSalesStatus())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10301", new String[] {
                                                 CodedMessageUtils.getMessage("label.frameNumber")
                                               , bo.getFrameNo()}));
            }
        }

        List<String> frameNos = form.getGridDataList().stream()
                                    .map(SDM050102BO::getFrameNo)
                                    .toList();

        List<CmmUnitPromotionItemVO> cmmUnitPromotionItemVOList = sdm0501Service.getByframeNosAndPromotionListId(frameNos, form.getPromotionListId());

        sdm0501Service.doDelete(cmmUnitPromotionItemVOList);
    }
}
