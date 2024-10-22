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

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.McSalesStatus;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.unit.SDM050201BO;
import com.a1stream.domain.bo.unit.SDM050201RetrieveBO;
import com.a1stream.domain.bo.unit.SDM050202BO;
import com.a1stream.domain.bo.unit.SDM050202HeaderBO;
import com.a1stream.domain.bo.unit.SDM050202RetrieveBO;
import com.a1stream.domain.form.unit.SDM050201Form;
import com.a1stream.domain.form.unit.SDM050202Form;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.CmmUnitPromotionItemVO;
import com.a1stream.domain.vo.CmmUnitPromotionListVO;
import com.a1stream.domain.vo.CmmUnitPromotionModelVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.unit.service.SDM0502Service;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

@Component
public class SDM0502Facade {

    @Resource
    private SDM0502Service sdm0502Service;

    public static final String INVOICE_STRING                               = "invoice";
    public static final String GIFTRECEIPT_STRING                           = "giftReceipt";
    public static final String IDCARD_STRING                                = "idCard";
    public static final String REGISTRATIONCARD_STRING                      = "registrationCard";
    public static final String LUCKYDRAWVOUCHER_STRING                      = "luckyDrawVoucher";
    public static final String OTHER_STRING                                 = "other";

    public SDM050201RetrieveBO findSetUpPromotionTerms(SDM050201Form form) {

        SDM050201RetrieveBO sdm050201BO = new SDM050201RetrieveBO();
        if(ObjectUtils.isEmpty(form.getPromotionListId())) {
            //如果传递过来的
            CmmUnitPromotionListVO cmmUnitPromotionListVO = sdm0502Service.getNewUnitPromotionList();
            form.setPromotionListId(cmmUnitPromotionListVO.getPromotionListId());
        }
        CmmUnitPromotionListVO header = sdm0502Service.findByPromotionListId(form.getPromotionListId());
        List<SDM050201BO> detailList = sdm0502Service.findSetUpPromotionTerms(form);
        sdm050201BO.setHeader(header);
        sdm050201BO.setDetailList(detailList);
        return sdm050201BO;
    }

    public List<SDM050201BO> findSetUpPromotionTermsExport(SDM050201Form form) {

        return sdm0502Service.findSetUpPromotionTerms(form);
    }

    public SDM050201Form checkFile(SDM050201Form form, String siteId) {

        return setSreenListAboutSetUpPromotionTerms(form, siteId);
    }

    private SDM050201Form setSreenListAboutSetUpPromotionTerms(SDM050201Form form, String siteId) {

        List<SDM050201BO> importList = form.getImportList();

        if (CollectionUtils.isEmpty(importList)) {
            return form;
        }

        // 转大写 去除前后空格
        for (SDM050201BO formatBo : importList) {
            formatBo.setModelCd(formatStr(formatBo.getModelCd()));
        }

        Set<String> seen = new HashSet<>();
        //model
        Map<String, MstProductVO> productMap = getSetUpPromoTermsProductMap(siteId, importList);

        for (SDM050201BO bo : importList) {

            // 设置error信息
            setErrorInfoAboutSetUpPromotionTerms(seen
                                               , productMap
                                               , bo);

            if(productMap.containsKey(bo.getModelCd())) {
                bo.setModelCd(productMap.get(bo.getModelCd()).getProductCd());
                bo.setModelNm(productMap.get(bo.getModelCd()).getSalesDescription());
                bo.setModelId(productMap.get(bo.getModelCd()).getProductId());
                bo.setStatus(CommonConstants.CHAR_FALSE);
                //新增标识
            }
        }
        return form;
    }

    private Map<String, MstProductVO> getSetUpPromoTermsProductMap(String siteId, List<SDM050201BO> detaList) {

        Map<String, MstProductVO> vomap = new HashMap<>();
        List<String> siteList = List.of(CommonConstants.CHAR_DEFAULT_SITE_ID, siteId);

        //当有一条modelCd数据为空时，报错
        Set<String> productCds = detaList.stream()
                .filter(bo -> {
                    if (StringUtils.isBlank(bo.getModelCd())) {
                        throw new BusinessCodedException(ComUtil.t("M.E.10317", new String[] { ComUtil.t("label.modelCode") }));
                    }
                    return true;
                })
                .map(SDM050201BO::getModelCd)
                .collect(Collectors.toSet());

        if(!siteList.isEmpty() && !productCds.isEmpty()) {
            vomap = sdm0502Service.getProductByCds(productCds, siteList).stream().collect(Collectors.toMap(v -> v.getProductCd(),Function.identity()));
        }
        return vomap;
    }

    private void setErrorInfoAboutSetUpPromotionTerms(Set<String> seen
            , Map<String, MstProductVO> productMap
            , SDM050201BO bo) {

        StringBuilder errorMsg = new StringBuilder();
        List<String> error = new ArrayList<>();

        // check1: 如果Model Code为空或者Model Code不存在
        if(StringUtils.isBlank(bo.getModelCd()) || !productMap.containsKey(bo.getModelCd())) {

            errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                                            , new Object[]{CodedMessageUtils.getMessage("label.modelCode")
                                            , bo.getModelCd()
                                            , CodedMessageUtils.getMessage("label.tableProduct")}));
        }

        // check2: 导入数据Model Code重复数据
        String key = bo.getModelCd();
        if(!seen.add(key)) {

            errorMsg.append(CodedMessageUtils.getMessage("M.E.00304"
                                            , new Object[]{ CodedMessageUtils.getMessage("label.modelNo")
                                            , bo.getModelCd()}));
        }

        bo.setErrorMessage(errorMsg.toString());
        if (errorMsg.length() > 0) {
            error.add(errorMsg.toString());
        }
        bo.setError(error);
    }

    //SDM050201 更新导入数据
    public void doConfirm(SDM050201Form form, String siteId) {

        //更新校验数据准备
        Set<String> seen = new HashSet<>();
        Map<String, MstProductVO> modelMap = getSetUpPromoTermsProductMap(siteId, form.getGridDataList());
        List<CmmUnitPromotionItemVO> cmmUnitPromotionItemVOs = sdm0502Service.findItemByPromotionListId(form.getPromotionListId());
        List<Long> modelIds = cmmUnitPromotionItemVOs.stream()
                                .map(CmmUnitPromotionItemVO::getProductId)
                                .toList();
        //更新校验
        this.confirmCheck(form, modelMap, modelIds, seen);

        this.confirmSetupPromoTerms(form, modelIds);
    }

    private void confirmSetupPromoTerms(SDM050201Form form, List<Long> modelIds) {
        //更新
        //删除
        List<CmmUnitPromotionModelVO> cmmUnitPromotionModelVOs = sdm0502Service.findByPromotionListIdAndProductId(form.getPromotionListId(), modelIds);

        List<MstProductVO> mstProductVOs = sdm0502Service.getMstProduct(modelIds);
        List<String> modelCds = mstProductVOs.stream()
                            .map(MstProductVO::getProductCd)
                            .toList();

        List<CmmUnitPromotionModelVO> deleteVOs = new ArrayList<>();
        List<CmmUnitPromotionListVO> listAddVOs = new ArrayList<>();
        List<CmmUnitPromotionModelVO> modelAddVOs = new ArrayList<>();

        CmmUnitPromotionListVO cmmUnitPromotionListVO;
        if(!ObjectUtils.isEmpty(form.getPromotionListId())) {
            cmmUnitPromotionListVO = sdm0502Service.findByPromotionListId(form.getPromotionListId());
            cmmUnitPromotionListVO.setPromotionCd(cmmUnitPromotionListVO.getPromotionCd());
            cmmUnitPromotionListVO.setPromotionRetrieve((form.getPromotionCd()+form.getPromotion()).toUpperCase().replace(" ", ""));
        }else {
            cmmUnitPromotionListVO = CmmUnitPromotionListVO.create();
            cmmUnitPromotionListVO.setModifyFlag(CommonConstants.CHAR_N);
            cmmUnitPromotionListVO.setBatchFlag(CommonConstants.CHAR_N);
            cmmUnitPromotionListVO.setUpdateCount(CommonConstants.INTEGER_ZERO);
            cmmUnitPromotionListVO.setUpdateProgram("SDM0502");
            sdm0502Service.newPromotionCd(cmmUnitPromotionListVO);
            cmmUnitPromotionListVO.setPromotionRetrieve((cmmUnitPromotionListVO.getPromotionCd()+form.getPromotion()).toUpperCase().replace(" ", ""));
        }
        cmmUnitPromotionListVO.setPromotionNm(form.getPromotion());
        cmmUnitPromotionListVO.setInvoice(form.getRequiredDocument().contains(INVOICE_STRING)?CommonConstants.CHAR_Y:CommonConstants.CHAR_N);
        cmmUnitPromotionListVO.setRegistrationCard(form.getRequiredDocument().contains(REGISTRATIONCARD_STRING)?CommonConstants.CHAR_Y:CommonConstants.CHAR_N);
        cmmUnitPromotionListVO.setGiftReceipt(form.getRequiredDocument().contains(GIFTRECEIPT_STRING)?CommonConstants.CHAR_Y:CommonConstants.CHAR_N);
        cmmUnitPromotionListVO.setGiftMax(form.getGiftMax());
        cmmUnitPromotionListVO.setGiftNm1(form.getGiftNm1());
        cmmUnitPromotionListVO.setGiftNm2(form.getGiftNm2());
        cmmUnitPromotionListVO.setGiftNm3(form.getGiftNm3());
        cmmUnitPromotionListVO.setLuckyDrawVoucher(form.getRequiredDocument().contains(LUCKYDRAWVOUCHER_STRING)?CommonConstants.CHAR_Y:CommonConstants.CHAR_N);
        cmmUnitPromotionListVO.setIdCard(form.getRequiredDocument().contains(IDCARD_STRING)?CommonConstants.CHAR_Y:CommonConstants.CHAR_N);
        cmmUnitPromotionListVO.setOthersFlag(form.getRequiredDocument().contains(OTHER_STRING)?CommonConstants.CHAR_Y:CommonConstants.CHAR_N);
        cmmUnitPromotionListVO.setFromDate(form.getDateFrom());
        cmmUnitPromotionListVO.setToDate(form.getDateTo());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);
        cmmUnitPromotionListVO.setUploadEndDate(LocalDate.parse(form.getDateTo(), formatter).plusDays(7).format(formatter));
        listAddVOs.add(cmmUnitPromotionListVO);

        int i = 0;
        for(SDM050201BO bo: form.getGridDataList()) {

            if(CommonConstants.CHAR_TRUE.equals(bo.getStatus()) && modelCds.contains(bo.getModelCd())) {
                //行状态为删除时 判断是否为当前车型
                deleteVOs.add(cmmUnitPromotionModelVOs.get(i));
            }else if (CommonConstants.CHAR_FALSE.equals(bo.getStatus())) {
                    //有数据 更新CmmUnitPromotionList表、新增CmmUnitPromotionModel

                CmmUnitPromotionModelVO cmmUnitPromotionModelVO = CmmUnitPromotionModelVO.create();
                if(!ObjectUtils.isEmpty(form.getPromotionListId())) {
                    cmmUnitPromotionModelVO.setPromotionListId(form.getPromotionListId());
                }else {
                    cmmUnitPromotionModelVO.setPromotionListId(listAddVOs.get(0).getPromotionListId());
                }
                cmmUnitPromotionModelVO.setProductId(bo.getModelId());
                cmmUnitPromotionModelVO.setProductCd(bo.getModelCd());
                cmmUnitPromotionModelVO.setProductNm(bo.getModelNm());
                cmmUnitPromotionModelVO.setUpdateProgram("SDM0502");
                modelAddVOs.add(cmmUnitPromotionModelVO);
            }
            i++;
        }

        sdm0502Service.doConfirm(deleteVOs, listAddVOs, modelAddVOs);
    }

    public void confirmCheck(SDM050201Form form, Map<String, MstProductVO> modelMap, List<Long> modelIds, Set<String> seen) {

        //条件部校验
        // confirmCheck1: Required Document的check box 至少勾选一个
        if(CollectionUtils.isEmpty(form.getRequiredDocument())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10297"));
        }

        // confirmCheck2: GiftReceipt的check box 被勾选，gift max 必须有值
        if (form.getRequiredDocument().contains(GIFTRECEIPT_STRING) && ObjectUtils.isEmpty(form.getGiftMax())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10304"));
        }

        // confirmCheck2: 根据 gift max 设置 gift name，确保不为空
        if ( form.getRequiredDocument().contains(GIFTRECEIPT_STRING) &&
                ((form.getGiftMax().equals(CommonConstants.INTEGER_ONE) && StringUtils.isEmpty(form.getGiftNm1()))
              || (form.getGiftMax().equals(CommonConstants.INTEGER_TWO)
                      && (StringUtils.isEmpty(form.getGiftNm1()) || StringUtils.isEmpty(form.getGiftNm2())))
              || (form.getGiftMax().equals(CommonConstants.INTEGER_THREE)
                      && (StringUtils.isEmpty(form.getGiftNm1()) || StringUtils.isEmpty(form.getGiftNm2()) || StringUtils.isEmpty(form.getGiftNm3()))))) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10304"));
        }

        // confirmCheck3: 失效日必须大于当前日期
        LocalDate dateFrom = LocalDate.parse(form.getDateFrom(), DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD));
        LocalDate dateTo = LocalDate.parse(form.getDateTo(), DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD));

        if (dateFrom.isAfter(dateTo)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00205", new String[] {
                                             CodedMessageUtils.getMessage("label.toDate"),
                                             CodedMessageUtils.getMessage("label.fromDate")}));
        }

        // confirmCheck4: 间隔不超过一年
        if (dateFrom.plusMonths(CommonConstants.INTEGER_TWELVE).isBefore(dateTo)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10296", new String[] {
                                             CodedMessageUtils.getMessage("label.orderDateFrom"),
                                             CodedMessageUtils.getMessage("label.orderDateTo")}));
        }

        //明细部校验
        for(SDM050201BO bo: form.getGridDataList()) {
            // confirmCheck5: 重复行校验
            String key = bo.getModelCd().toUpperCase();
            if(!seen.add(key)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10297"));
            }

            // confirmCheck6: 存在性验证
            if(ObjectUtils.isEmpty(modelMap.get(key))) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                                 CodedMessageUtils.getMessage("label.model"),
                                                 bo.getModelCd(),
                                                 CodedMessageUtils.getMessage("label.productInformation")}));
            }

            // confirmCheck7: 当明细行被删除时，用当前的PromotionListId和ModelId去cmm_unit_promotion_item 查询， 有值则报错
            if(CommonConstants.CHAR_TRUE.equals(bo.getStatus()) && modelIds.contains(bo.getModelId())) {
                //行状态 true表示删除
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10305"), new String[] {
                                                 CodedMessageUtils.getMessage("label.modelCode"),
                                                 bo.getModelCd(),
                                                 CodedMessageUtils.getMessage("label.tableUnitPromotionModel")});
            }
        }
    }

    public SDM050202RetrieveBO findUploadPromoQC(SDM050202Form form) {

        SDM050202HeaderBO header = sdm0502Service.findUploadPromoQCHeader(form);
        List<SDM050202BO> detailList = sdm0502Service.findUploadPromoQC(form);
        SDM050202RetrieveBO sdm050202RetrieveBO = new SDM050202RetrieveBO();
        sdm050202RetrieveBO.setDetailList(detailList);
        sdm050202RetrieveBO.setHeader(header);
        return sdm050202RetrieveBO;
    }

    public SDM050202Form getCheckUploadPromoQCFile(SDM050202Form form, String siteId) {

        // 将导入的数据和error信息设置到画面上
        return setSreenList(form, siteId);
    }

    private SDM050202Form setSreenList(SDM050202Form form, String siteId) {

        List<SDM050202BO> importList = form.getImportList();

        if (CollectionUtils.isEmpty(importList)) {
            return form;
        }

        //获取唯一一条Promotion信息
        CmmUnitPromotionListVO cmmUnitPromotionListVO = sdm0502Service.findByPromotionListId(Long.valueOf(form.getOtherProperty().toString()));

        // 转大写 去除前后空格
        for (SDM050202BO formatBo : importList) {
            formatBo.setDealerCd(formatStr(formatBo.getDealerCd()));
            formatBo.setPointCd(formatStr(formatBo.getPointCd()));
            formatBo.setModelCd(formatStr(formatBo.getModelCd()));
            formatBo.setFrameNo(formatStr(formatBo.getFrameNo()));
        }

        Set<String> seen = new HashSet<>();
        //dealer 直接用dealer code对应表site id查询
        Map<String, CmmSiteMasterVO> dealerMap = getDealerMap(importList);
        //point
        Map<String, MstFacilityVO> facilityMap = getFacilityMap(siteId, importList);
        //model
        Map<String, MstProductVO> productMap = getProductMap(siteId, importList);
        //frameNo
        Map<String, CmmSerializedProductVO> frameNoMap = getFrameNoMap(importList);

        List<CmmUnitPromotionModelVO> cmmUnitPromotionModelVOs = sdm0502Service.findFrameNoByPromotionListId(Long.valueOf(form.getOtherProperty().toString()));
        List<String> productCds = cmmUnitPromotionModelVOs.stream()
                                 .map(CmmUnitPromotionModelVO::getProductCd)
                                 .toList();

        for (SDM050202BO bo : importList) {

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

    private Map<String, CmmSiteMasterVO> getDealerMap(List<SDM050202BO> detaiList) {

        Map<String, CmmSiteMasterVO> vomap = new HashMap<>();
        Set<String> dealerCds = detaiList.stream().map(SDM050202BO::getDealerCd).collect(Collectors.toSet());
        if(!dealerCds.isEmpty()) {
            vomap = sdm0502Service.getDealerByCd(dealerCds).stream().collect(Collectors.toMap(v -> v.getSiteCd(),Function.identity()));
        }
        return vomap;
    }

    private Map<String, MstFacilityVO> getFacilityMap(String siteId, List<SDM050202BO> detaiList) {

        Map<String, MstFacilityVO> vomap = new HashMap<>();
        Set<String> facilityCds = detaiList.stream().map(SDM050202BO::getPointCd).collect(Collectors.toSet());
        if(!facilityCds.isEmpty()) {
            vomap = sdm0502Service.getFacilityByCd(facilityCds, siteId).stream().collect(Collectors.toMap(v -> v.getFacilityCd(),Function.identity()));
        }
        return vomap;
    }

    private Map<String, MstProductVO> getProductMap(String siteId, List<SDM050202BO> detaiList) {

        Map<String, MstProductVO> vomap = new HashMap<>();
        List<String> siteList = List.of(CommonConstants.CHAR_DEFAULT_SITE_ID, siteId);
        Set<String> productCds = detaiList.stream().map(SDM050202BO::getModelCd).collect(Collectors.toSet());

        if(!siteList.isEmpty() && !productCds.isEmpty()) {
            vomap = sdm0502Service.getProductByCds(productCds, siteList).stream().collect(Collectors.toMap(v -> v.getProductCd(),Function.identity()));
        }
        return vomap;
    }

    private Map<String, CmmSerializedProductVO> getFrameNoMap(List<SDM050202BO> detaiList) {

        Map<String, CmmSerializedProductVO> vomap = new HashMap<>();
        Set<String> frameNos = detaiList.stream().map(SDM050202BO::getFrameNo).collect(Collectors.toSet());

        if(!frameNos.isEmpty()) {
            vomap = sdm0502Service.findByFrameNos(frameNos).stream().collect(Collectors.toMap(v -> v.getFrameNo(),Function.identity()));
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
            , SDM050202BO bo) {

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

    public void doConfirmUploadPromoQC(SDM050202Form form) {

        List<CmmUnitPromotionItemVO> cmmUnitPromotionItemVOList = new ArrayList<>();
        List<SDM050202BO> detaiList = form.getGridDataList();
        Map<String, CmmSerializedProductVO> frameNoMap = getFrameNoMap(detaiList);
        List<CmmUnitPromotionItemVO> cmmUnitPromotionItemVOs = sdm0502Service.findItemByPromotionListId(form.getPromotionListId());
        Set<String> frameNos = cmmUnitPromotionItemVOs.stream()
                              .map(CmmUnitPromotionItemVO::getFrameNo) // 替换为实际的获取 frameNo 的方法
                              .filter(frameNo -> frameNo != null) // 过滤掉 null 值（可选）
                              .distinct() // 去重
                              .collect(Collectors.toSet());

        //新增表cmm_unit_promotion_item
        for(SDM050202BO bo : detaiList) {
            if(!frameNos.contains(bo.getFrameNo())) {
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
        CmmUnitPromotionListVO cmmUnitPromotionListVO = sdm0502Service.findByPromotionListId(form.getPromotionListId());
        cmmUnitPromotionListVO.setModifyFlag(CommonConstants.CHAR_Y);

        sdm0502Service.doConfirmUploadPromoQC(cmmUnitPromotionItemVOList, cmmUnitPromotionListVO);
    }

}
