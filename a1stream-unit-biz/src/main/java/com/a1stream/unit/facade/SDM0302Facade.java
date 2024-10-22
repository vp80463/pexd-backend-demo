package com.a1stream.unit.facade;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.bo.SDCommonCheckBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.BatteryType;
import com.a1stream.common.constants.PJConstants.PurchaseType;
import com.a1stream.common.constants.PJConstants.RegistrationDocumentFeatrueCategory;
import com.a1stream.domain.bo.unit.SDM030201BO;
import com.a1stream.domain.form.unit.SDM030201Form;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.ConsumerPrivacyPolicyResultVO;
import com.a1stream.domain.vo.ConsumerPrivateDetailVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.unit.service.SDM0302Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: Retail Order Entry
*
* mid2303
* 2024年9月29日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/09/29   Ruan Hansheng   New
*/
@Component
public class SDM0302Facade {

    @Resource
    private SDM0302Service sdm0302Service;

    public SDM030201BO getInitResult(SDM030201Form form) {

        SDM030201BO formData = form.getFormData();
        SDM030201BO result = new SDM030201BO();

        if (ObjectUtils.isEmpty(formData.getSalesOrderId())) {
            MstFacilityVO mstFacilityVO = sdm0302Service.getMstFacilityVO(formData.getFacilityId());
            result.setPoint(mstFacilityVO.getFacilityCd() + CommonConstants.CHAR_SPACE + mstFacilityVO.getFacilityNm());
            result.setPointId(mstFacilityVO.getFacilityId());
            result.setPointCd(mstFacilityVO.getFacilityCd());
            result.setPointNm(mstFacilityVO.getFacilityNm());
            result.setSalesDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
            return result;
        }

        SalesOrderVO salesOrderVO = sdm0302Service.getSalesOrderVO(formData.getSalesOrderId());
        result.setSalesOrderId(formData.getSalesOrderId());
        result.setPointId(salesOrderVO.getFacilityId());
        result.setSalesDate(salesOrderVO.getOrderDate());
        result.setOrderNo(salesOrderVO.getOrderNo());
        result.setModelCd(salesOrderVO.getModelCd());
        result.setModelNm(salesOrderVO.getModelNm());
        result.setFrameNo(salesOrderVO.getFrameNo());
        result.setEngineNo(salesOrderVO.getEngineNo());
        result.setColorNm(salesOrderVO.getColorNm());
        result.setOwnerCusTaxCode(salesOrderVO.getCustomerTaxCode());
        result.setSerializedProductId(salesOrderVO.getSerializedProductId());
        result.setOwnerConsumerId(salesOrderVO.getCmmConsumerId());
        result.setOwnerLastNm(salesOrderVO.getConsumerNmLast());
        result.setOwnerMiddleNm(salesOrderVO.getConsumerNmMiddle());
        result.setOwnerFirstNm(salesOrderVO.getConsumerNmFirst());
        result.setOwnerMobile(salesOrderVO.getMobilePhone());
        result.setUserConsumerId(salesOrderVO.getUserConsumerId());
        result.setEvFlag(salesOrderVO.getEvOrderFlag());
        result.setModelType(salesOrderVO.getModelType());
        result.setDisplacement(salesOrderVO.getDisplacement());
        result.setOrderStatus(salesOrderVO.getOrderStatus());
        result.setModel(salesOrderVO.getModelCd() + CommonConstants.CHAR_SPACE + salesOrderVO.getModelNm());
        
        MstFacilityVO mstFacilityVO = sdm0302Service.getMstFacilityVO(salesOrderVO.getFacilityId());
        result.setPoint(mstFacilityVO.getFacilityCd() + CommonConstants.CHAR_SPACE + mstFacilityVO.getFacilityNm());
        result.setPointId(mstFacilityVO.getFacilityId());
        result.setPointCd(mstFacilityVO.getFacilityCd());
        result.setPointNm(mstFacilityVO.getFacilityNm());

        List<SalesOrderItemVO> salesOrderItemVOList = sdm0302Service.getSalesOrderItemVOList(formData.getSalesOrderId());
        SalesOrderItemVO salesOrderItemVO = salesOrderItemVOList.get(0);
        result.setProductId(salesOrderItemVO.getProductId());

        if (CommonConstants.CHAR_Y.equals(result.getEvFlag())) {
            BatteryVO batteryVO1 = sdm0302Service.getBatteryVO(salesOrderVO.getSerializedProductId(), BatteryType.TYPE1.getCodeDbid());
            if (null != batteryVO1) {
                result.setBatteryNo1(batteryVO1.getBatteryNo());
                result.setBatteryId1(batteryVO1.getBatteryId());
            }
    
            BatteryVO batteryVO2 = sdm0302Service.getBatteryVO(salesOrderVO.getSerializedProductId(), BatteryType.TYPE2.getCodeDbid());
            if (null != batteryVO2) {
                result.setBatteryNo2(batteryVO2.getBatteryNo());
                result.setBatteryId2(batteryVO2.getBatteryId());
            }
        }

        CmmConsumerVO owner = sdm0302Service.getCmmConsumerVO(salesOrderVO.getCmmConsumerId());
        result.setOwnerSns(owner.getSns());
        result.setOwnerGender(owner.getGender());
        result.setOwnerBirthYear(owner.getBirthYear());
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int ownerAge = currentYear - Integer.parseInt(owner.getBirthYear());
        result.setOwnerAge(String.valueOf(ownerAge));
        result.setOwnerBirthDate(owner.getBirthDate());
        result.setOwnerProvince(owner.getProvinceGeographyId());
        result.setOwnerDistrict(owner.getCityGeographyId());
        result.setOwnerAddress(owner.getAddress());
        result.setOwnerEmail(owner.getEmail());

        ConsumerPrivacyPolicyResultVO consumerPrivacyPolicyResultVO = sdm0302Service.getConsumerPrivacyPolicyResultVO(owner.getConsumerId());
        result.setPolicyResultFlag(consumerPrivacyPolicyResultVO.getAgreementResult());

        CmmConsumerVO user = sdm0302Service.getCmmConsumerVO(salesOrderVO.getUserConsumerId());
        if (null != user) {
            result.setUserLastNm(user.getLastNm());
            result.setUserMiddleNm(user.getMiddleNm());
            result.setUserFirstNm(user.getFirstNm());
            result.setUserGender(user.getGender());
            result.setUserBirthYear(user.getBirthYear());
            int userAge = currentYear - Integer.parseInt(user.getBirthYear());
            result.setUserAge(String.valueOf(userAge));
            result.setUserBirthDate(user.getBirthDate());
            result.setUserProvince(user.getProvinceGeographyId());
            result.setUserDistrict(user.getCityGeographyId());
    
            ConsumerPrivateDetailVO userDetail = sdm0302Service.getConsumerPrivateDetailVO(user.getConsumerRetrieve());
            result.setUserMobile(userDetail.getMobilePhone());
        }

        result.setUseType(RegistrationDocumentFeatrueCategory.USETYPE001.getCodeDbid());
        result.setOwnerType(RegistrationDocumentFeatrueCategory.OWNERTYPE003.getCodeDbid());
        result.setPurchaseType(PurchaseType.NEW.getCodeDbid());

        return result;
    }

    public SDM030201BO getMotorcycleInfo(SDM030201Form form) {

        SDM030201BO formData = form.getFormData();
        SDM030201BO result = new SDM030201BO();
        SerializedProductVO serializedProductVO = sdm0302Service.getSerializedProductVO(form.getSiteId(), formData.getFrameNo());
        if (null == serializedProductVO) {
            return result;
        }
        result.setEvFlag(serializedProductVO.getEvFlag());
        result.setFrameNo(serializedProductVO.getFrameNo());
        result.setEngineNo(serializedProductVO.getEngineNo());
        result.setSerializedProductId(serializedProductVO.getSerializedProductId());

        MstProductVO mstProductVO = sdm0302Service.getMstProductVO(serializedProductVO.getProductId());
        if (null != mstProductVO) {
            result.setProductId(mstProductVO.getProductId());
            result.setModelCd(mstProductVO.getProductCd());
            result.setModelNm(mstProductVO.getSalesDescription());
            result.setColorNm(mstProductVO.getColorNm());
            result.setModelType(mstProductVO.getModelType());
            result.setDisplacement(mstProductVO.getDisplacement());
            result.setModel(mstProductVO.getProductCd() + CommonConstants.CHAR_SPACE + mstProductVO.getSalesDescription());
        }

        if (CommonConstants.CHAR_Y.equals(result.getEvFlag())) {
            BatteryVO batteryVO1 = sdm0302Service.getBatteryVO(serializedProductVO.getSerializedProductId(), BatteryType.TYPE1.getCodeDbid());
            if (null != batteryVO1) {
                result.setBatteryNo1(batteryVO1.getBatteryNo());
                result.setBatteryId1(batteryVO1.getBatteryId());
            }
            BatteryVO batteryVO2 = sdm0302Service.getBatteryVO(serializedProductVO.getSerializedProductId(), BatteryType.TYPE2.getCodeDbid());
            if (null != batteryVO2) {
                result.setBatteryNo2(batteryVO2.getBatteryNo());
                result.setBatteryId2(batteryVO2.getBatteryId());
            }
        }

        return result;
    }

    public void check(SDM030201Form form) {

        SDM030201BO formData = form.getFormData();

        if (StringUtils.isBlankText(formData.getOwnerBirthYear())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10333", new String[] { CodedMessageUtils.getMessage("label.yearOfBirth"), CommonConstants.CHAR_FOUR }));
        }

        if (formData.getOwnerBirthYear().length() != CommonConstants.INTEGER_FOUR) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10333", new String[] { CodedMessageUtils.getMessage("label.yearOfBirth"), CommonConstants.CHAR_FOUR }));
        }

        if (StringUtils.isNotBlankText(formData.getUserBirthYear()) && formData.getUserBirthYear().length() != CommonConstants.INTEGER_FOUR) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10333", new String[] { CodedMessageUtils.getMessage("label.yearOfBirth"), CommonConstants.CHAR_FOUR }));
        }

        String emailFormat = CommonConstants.EMAIL_REG_FORMAT;
        Pattern pattern = Pattern.compile(emailFormat);
        Matcher matcher = pattern.matcher(formData.getOwnerEmail());
        if (!matcher.matches()) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10311", new String[] { formData.getOwnerEmail() }));
        }

        if (formData.getOwnerMobile().length() > CommonConstants.INTEGER_TEN) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10330", new String[] { formData.getOwnerMobile() }));
        }

        if (StringUtils.isNotBlankText(formData.getUserMobile()) && formData.getUserMobile().length() > CommonConstants.INTEGER_TEN) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10330", new String[] { formData.getUserMobile() }));
        }

        SDCommonCheckBO checkBO = new SDCommonCheckBO();
        checkBO.setSiteId(form.getSiteId());
        checkBO.setPointCd(formData.getPointCd());
        checkBO.setFrameNo(formData.getFrameNo());
        checkBO.setModelCd(formData.getModelCd());
        checkBO.setSalesDate(formData.getSalesDate());
        checkBO.setModelId(formData.getProductId());
        checkBO.setPointId(formData.getPointId());

        sdm0302Service.checkSerializedProductCorrelation(checkBO);
        if (CommonConstants.CHAR_Y.equals(formData.getEvFlag())) {
            sdm0302Service.checkBatteryStatus(formData.getBatteryNo1(), formData.getBatteryNo2(), formData.getBatteryId1(), formData.getBatteryId2());
        }

    }

    public SDM030201BO delivery(SDM030201Form form) {

        return sdm0302Service.delivery(form);
    }
}
