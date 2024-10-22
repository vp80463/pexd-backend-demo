package com.a1stream.common.manager;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.SeqConstants;
import com.a1stream.common.constants.SeqConstants.SeqNoType;
import com.a1stream.domain.entity.MstFacility;
import com.a1stream.domain.entity.MstSeqNoInfo;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstSeqNoInfoRepository;
import com.a1stream.domain.repository.MstSeqNoSettingRepository;
import com.a1stream.domain.vo.MstSeqNoInfoVO;
import com.a1stream.domain.vo.MstSeqNoSettingVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Component
public class GenerateNoManager {

    @Resource
    private MstSeqNoSettingRepository mstSeqNoSettingRepo;

    @Resource
    private MstSeqNoInfoRepository mstSeqNoInfoRepo;

    @Resource
    private MstFacilityRepository mstFacilityRepo;

    public String generateSlipNo(String siteId, Long stockPointId) {
        return generateSequenceNumber(siteId, stockPointId, SeqNoType.SLIP_NO);
    }

    public String generatelocationTransNo(String siteId, Long stockPointId) {
        return generateSequenceNumber(siteId, stockPointId, SeqNoType.LOCATION_TRANS_NO);
    }

    public String generateInvoiceNo(String siteId) {
        return generateSequenceNumber(siteId, null, SeqNoType.INVOICE_NO);
    }

    public String generateReturnInvoiceNo(String siteId) {
        return generateSequenceNumber(siteId, null, SeqNoType.RETURN_INVOICENO);
    }
    public String generateDeliveryNo(String siteId, Long stockPointId) {
        return generateSequenceNumber(siteId, stockPointId, SeqNoType.DELIVERY_NO);
    }

    public String generateNonSerializedItemPurchaseOrderNo(String siteId, Long stockPointId) {

        return generateSequenceNumber(siteId, stockPointId, SeqNoType.NONSERIAL_PURCHASEORDERNO);
    }

    public String generatePickingListNo(String siteId, Long stockPointId) {
        return generateSequenceNumber(siteId, stockPointId, SeqNoType.PICKING_LIST_NO);
    }

    public String generateNonSerializedItemStoringListNo(String siteId, Long stockPointId) {
        return generateSequenceNumber(siteId, stockPointId, SeqNoType.NONSERIAL_STORINGLISTNO);
    }

    public String generateFastShippingReportSalesOrderNo(String siteId, Long pointId) {
        return generateSequenceNumber(siteId, pointId, SeqNoType.FAST_SHIPPING_SO);
    }

    public String generateNonSerializedItemSalesOrderNo(String siteId, Long pointId) {
        return generateSequenceNumber(siteId, pointId, SeqNoType.NONSERIAL_SALESORDERNO);
    }

    public String generateNonSerializedItemStoringLineNo(String siteId, Long stockPointId) {
        return generateSequenceNumber(siteId, stockPointId, SeqNoType.NONSERIAL_STORINGLINENO);
    }

    public String generateBarreryServiceOrderNo(String siteId, Long pointId) {
        return generateSequenceNumber(siteId, pointId, SeqNoType.BARRERY_SERVICEORDERNO);
    }

    public String generateServiceRequestNo(String siteId, Long pointId) {
        return generateSequenceNumber(siteId, pointId, SeqNoType.SERVICE_SERVICEREQUESTNO);
    }

    public String generateSequenceNumber(String siteId, Long facilityId, String seqNoType) {

        if (StringUtils.isEmpty(seqNoType)) {
            throw new BusinessCodedException("Argument 'seqNoType' is required!");
        }

        MstSeqNoSettingVO mstSeqNoSetting = mstSeqNoSettingRepo.getUsableSeqNoSetting(seqNoType, siteId, facilityId);
        if (mstSeqNoSetting == null) {
            throw new BusinessCodedException("Can not find the 'MstSeqNoSetting' records from DB.");
        }

        String formula = mstSeqNoSetting.getFormula();
        if (StringUtils.isEmpty(formula)) {
            String msg = "The formula for generating sequence number has not been defined yet. Please have a try after defining it.";

            throw new BusinessCodedException(msg);
        }

        String[] formulaElements = formula.split(SeqConstants.FORMULA_ELEMENT_DELIMITER);

        StringBuffer generateNo = new StringBuffer();
        geteGenerateNo(siteId, facilityId, generateNo, mstSeqNoSetting, formulaElements);

        return generateNo.toString();
    }

    private void geteGenerateNo(String siteId
                                , Long facilityId
                                , StringBuffer generateNo
                                , MstSeqNoSettingVO mstSeqNoSetting
                                , String[] formulaElements) {

        String prefix = "";
        // replace placeholder by concrete value
        for (String eachFormulaElement : formulaElements) {
            eachFormulaElement = eachFormulaElement.replaceAll("[{}]", "");
            if (StringUtils.equals(SeqConstants.FORMULA_SEQ_NO, eachFormulaElement)) { // {seqNo}
                generateNo.append(getSeqNo(mstSeqNoSetting, prefix, siteId, facilityId));
            } else if (StringUtils.equals(SeqConstants.FORMULA_SITE, eachFormulaElement)) { // {site}
                generateNo.append(siteId);
            } else if (StringUtils.equals(SeqConstants.FORMULA_FACILITY, eachFormulaElement)) {// {facNoId}
                generateNo.append(getFacilityId(facilityId));
            } else if(SeqConstants.FORMULA_DATE_TYPE.contains(eachFormulaElement)) { // 日期格式
                generateNo.append(nowDate(eachFormulaElement));
            } else {
                prefix = eachFormulaElement;
                generateNo.append(eachFormulaElement);
            }
        }
    }

    private String getFacilityId(Long facilityId) {
        if (facilityId != null) {
            MstFacility facility = mstFacilityRepo.findByFacilityId(facilityId);
//            if (facility == null) {
//                throw new BusinessCodedException("Can not find the 'MstFacility' records from DB.");
//            }
            return facility == null? "" : facility.getNumberingIdCd();
        }

        return "";
    }

    private String getSeqNo(MstSeqNoSettingVO mstSeqNoSetting, String prefix, String siteId, Long facilityId) {

        MstSeqNoInfoVO mstSeqNoInfoVO = mstSeqNoInfoRepo.getBySeqNoType(mstSeqNoSetting.getSeqNoType()
                                                                    , siteId
                                                                    , facilityId);
        int seqNoLength = mstSeqNoSetting.getSeqNoLength();
        if(mstSeqNoInfoVO == null) {
            mstSeqNoInfoVO = buildMstSeqNoInfo(mstSeqNoSetting, prefix, siteId, facilityId);
        }

        // 获取最新seqNo并保存
        int currentNumber = mstSeqNoInfoVO.getCurrentNumber();
        int maxNumber = mstSeqNoInfoVO.getMaxNumber();

        int latestNumber = currentNumber >= maxNumber? mstSeqNoInfoVO.getStartNumber() : currentNumber;
        latestNumber++;
        mstSeqNoInfoVO.setCurrentNumber(latestNumber);

        mstSeqNoInfoRepo.save(BeanMapUtils.mapTo(mstSeqNoInfoVO, MstSeqNoInfo.class));

        String latestNumberStr = StringUtils.toString(latestNumber);

        return StringUtils.zeroPadding(latestNumberStr, seqNoLength);
    }

    private MstSeqNoInfoVO buildMstSeqNoInfo(MstSeqNoSettingVO mstSeqNoSetting, String prefix, String siteId, Long facilityId) {

        MstSeqNoInfoVO mstSeqNoInfo = new MstSeqNoInfoVO();

        mstSeqNoInfo.setSiteId(siteId);
        mstSeqNoInfo.setFacilityId(facilityId);
        mstSeqNoInfo.setSeqNoType(mstSeqNoSetting.getSeqNoType());
        mstSeqNoInfo.setMaxNumber(mstSeqNoSetting.getMaxNumber());
        mstSeqNoInfo.setStartNumber(SeqConstants.INTEGER_ZERO);
        mstSeqNoInfo.setCurrentNumber(SeqConstants.INTEGER_ZERO);
        mstSeqNoInfo.setPrefix(prefix);

        return mstSeqNoInfo;
    }

    /**
     * 获取当前时间
     */
    private String nowDate(String eachFormulaElement) {
        // mm => MM
        eachFormulaElement = eachFormulaElement.replaceAll("mm", "MM");
        return DateUtils.getCurrentDateString(eachFormulaElement);
    }

    public String generatePromotionCd(Long promotionListId) {
        return generatePromotionCd(CommonConstants.CHAR_DEFAULT_SITE_ID,promotionListId, SeqNoType.SERIAL_PROMOTIONCODE);
    }

    public String generatePromotionCd(String siteId, Long facilityId, String seqNoType) {

        if (StringUtils.isEmpty(seqNoType)) {
            throw new BusinessCodedException("Argument 'seqNoType' is required!");
        }

        MstSeqNoSettingVO mstSeqNoSetting = mstSeqNoSettingRepo.getUsableSeqNoSetting(seqNoType, siteId, facilityId);
        if (mstSeqNoSetting == null) {
            throw new BusinessCodedException("Can not find the 'MstSeqNoSetting' records from DB.");
        }

        String formula = mstSeqNoSetting.getFormula();
        if (StringUtils.isEmpty(formula)) {
            String msg = "The formula for generating sequence number has not been defined yet. Please have a try after defining it.";

            throw new BusinessCodedException(msg);
        }

        String[] formulaElements = formula.split(SeqConstants.FORMULA_ELEMENT_DELIMITER);

        StringBuffer generateNo = new StringBuffer();
        geteGenerateNoPromotionCd(siteId, facilityId, generateNo, mstSeqNoSetting, formulaElements);

        return generateNo.toString();
    }

    private void geteGenerateNoPromotionCd(String siteId
            , Long facilityId
            , StringBuffer generateNo
            , MstSeqNoSettingVO mstSeqNoSetting
            , String[] formulaElements) {

        String prefix = "";
        // replace placeholder by concrete value
        for (String eachFormulaElement : formulaElements) {
            eachFormulaElement = eachFormulaElement.replaceAll("[{}]", "");
            if (StringUtils.equals(SeqConstants.FORMULA_SEQ_NO, eachFormulaElement)) { // {seqNo}
                generateNo.append(getSeqNoPromotionCd(mstSeqNoSetting, prefix, siteId, facilityId));
            } else if (StringUtils.equals(SeqConstants.FORMULA_SITE, eachFormulaElement)) { // {site}
                generateNo.append(siteId);
            } else if(SeqConstants.FORMULA_DATE_TYPE.contains(eachFormulaElement)) { // 日期格式
                generateNo.append(nowDate(eachFormulaElement));
            } else {
                prefix = eachFormulaElement;
                generateNo.append(eachFormulaElement);
            }
        }
    }

    private String getSeqNoPromotionCd(MstSeqNoSettingVO mstSeqNoSetting, String prefix, String siteId, Long facilityId) {

        MstSeqNoInfoVO mstSeqNoInfoVO = mstSeqNoInfoRepo.getBySeqNoType(mstSeqNoSetting.getSeqNoType());
        int seqNoLength = mstSeqNoSetting.getSeqNoLength();
        if(mstSeqNoInfoVO == null) {
            mstSeqNoInfoVO = buildMstSeqNoInfo(mstSeqNoSetting, prefix, siteId, facilityId);
        }

        // 获取最新seqNo并保存
        int currentNumber = mstSeqNoInfoVO.getCurrentNumber();
        int maxNumber = mstSeqNoInfoVO.getMaxNumber();

        int latestNumber = currentNumber >= maxNumber? mstSeqNoInfoVO.getStartNumber() : currentNumber;
        latestNumber++;
        mstSeqNoInfoVO.setCurrentNumber(latestNumber);

        mstSeqNoInfoRepo.save(BeanMapUtils.mapTo(mstSeqNoInfoVO, MstSeqNoInfo.class));

        String latestNumberStr = StringUtils.toString(latestNumber);

        return StringUtils.zeroPadding(latestNumberStr, seqNoLength);
    }
}