package com.a1stream.unit.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.bo.SDCommonCheckBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.Occupation;
import com.a1stream.common.constants.MstCodeConstants.PaymentMethodType;
import com.a1stream.common.constants.PJConstants.BatteryType;
import com.a1stream.common.constants.PJConstants.ConsumerType;
import com.a1stream.common.constants.PJConstants.PurchaseType;
import com.a1stream.common.constants.PJConstants.RegistrationDocumentFeatrueCategory;
import com.a1stream.domain.bo.unit.SDM030701BO;
import com.a1stream.domain.form.unit.SDM030701Form;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.ConsumerPrivacyPolicyResultVO;
import com.a1stream.domain.vo.ConsumerPrivateDetailVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductTaxVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.unit.service.SDM0307Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:  Retail Order Entry For DO
*
* mid2303
* 2024年10月14日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/10/14   Ruan Hansheng   New
*/
@Component
public class SDM0307Facade {

    @Resource
    private SDM0307Service sdm0307Service;

    public SDM030701BO getInitResult(SDM030701Form form) {

        SDM030701BO formData = form.getFormData();
        SDM030701BO result = new SDM030701BO();

        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);

        if (ObjectUtils.isEmpty(formData.getSalesOrderId())) {
            MstFacilityVO mstFacilityVO = sdm0307Service.getMstFacilityVO(formData.getFacilityId());
            result.setMultiAddressFlag(mstFacilityVO.getMultiAddressFlag());
            result.setPoint(mstFacilityVO.getFacilityCd() + CommonConstants.CHAR_SPACE + mstFacilityVO.getFacilityNm());
            result.setPointId(mstFacilityVO.getFacilityId());
            result.setPointCd(mstFacilityVO.getFacilityCd());
            result.setPointNm(mstFacilityVO.getFacilityNm());
            result.setSalesDate(sysDate);
            result.setConsumerType(ConsumerType.BIKEPURCHASECUSTOMER.getCodeDbid());
            result.setUConsumerType(ConsumerType.BIKEPURCHASECUSTOMER.getCodeDbid());
            result.setOccupation(Occupation.NA);
            result.setPdiDate(sysDate);
            result.setPaymentMethod(PaymentMethodType.CASH);
            result.setSellingPrice(BigDecimal.ZERO);
            result.setTaxRate(BigDecimal.ZERO);
            result.setActualAmt(BigDecimal.ZERO);
            return result;
        }

        SalesOrderVO salesOrderVO = sdm0307Service.getSalesOrderVO(formData.getSalesOrderId());
        result.setSalesOrderId(formData.getSalesOrderId());
        result.setPointId(salesOrderVO.getFacilityId());
        result.setOrderNo(salesOrderVO.getOrderNo());
        result.setSalesPicNm(salesOrderVO.getSalesPicNm());
        result.setGiftDescription(salesOrderVO.getGiftDescription());
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
        result.setInvoicePrintFlag(salesOrderVO.getInvoicePrintFlag());
        result.setTotalQty(salesOrderVO.getTotalQty());
        result.setTotalAmt(salesOrderVO.getTotalAmt());
        result.setTotalActualQty(salesOrderVO.getTotalActualQty());
        result.setTotalActualAmt(salesOrderVO.getTotalActualAmt());
        result.setTotalActualAmtNotVat(salesOrderVO.getTotalActualAmtNotVat());
        result.setSpecialReduceFlag(salesOrderVO.getSpecialReduceFlag());
        result.setPaymentMethod(salesOrderVO.getPaymentMethodType());
        result.setDeliveryAddress(salesOrderVO.getFacilityMultiAddr());
        result.setOrderStatus(salesOrderVO.getOrderStatus());
        result.setModel(salesOrderVO.getModelCd() + CommonConstants.CHAR_SPACE + salesOrderVO.getModelNm());
        result.setOrderDate(salesOrderVO.getOrderDate());

        MstFacilityVO mstFacilityVO = sdm0307Service.getMstFacilityVO(salesOrderVO.getFacilityId());
        result.setMultiAddressFlag(mstFacilityVO.getMultiAddressFlag());
        result.setPoint(mstFacilityVO.getFacilityCd() + CommonConstants.CHAR_SPACE + mstFacilityVO.getFacilityNm());
        result.setPointId(mstFacilityVO.getFacilityId());
        result.setPointCd(mstFacilityVO.getFacilityCd());
        result.setPointNm(mstFacilityVO.getFacilityNm());

        List<SalesOrderItemVO> salesOrderItemVOList = sdm0307Service.getSalesOrderItemVOList(formData.getSalesOrderId());
        SalesOrderItemVO salesOrderItemVO = salesOrderItemVOList.get(0);
        result.setProductId(salesOrderItemVO.getProductId());
        result.setDiscountAmt(salesOrderItemVO.getDiscountAmt());
        result.setSellingPrice(salesOrderItemVO.getSellingPrice());
        result.setSellingPriceNotVat(salesOrderItemVO.getSellingPriceNotVat());
        result.setSpecialPrice(salesOrderItemVO.getSpecialPrice());
        result.setActualAmt(salesOrderItemVO.getActualAmt());
        result.setActualAmtNotVat(salesOrderItemVO.getActualAmtNotVat());

        if (CommonConstants.CHAR_Y.equals(result.getEvFlag())) {
            BatteryVO batteryVO1 = sdm0307Service.getBatteryVO(salesOrderVO.getSerializedProductId(), BatteryType.TYPE1.getCodeDbid());
            if (null != batteryVO1) {
                result.setBatteryNo1(batteryVO1.getBatteryNo());
                result.setBatteryId1(batteryVO1.getBatteryId());
            }
    
            BatteryVO batteryVO2 = sdm0307Service.getBatteryVO(salesOrderVO.getSerializedProductId(), BatteryType.TYPE2.getCodeDbid());
            if (null != batteryVO2) {
                result.setBatteryNo2(batteryVO2.getBatteryNo());
                result.setBatteryId2(batteryVO2.getBatteryId());
            }
        }

        CmmConsumerVO owner = sdm0307Service.getCmmConsumerVO(salesOrderVO.getCmmConsumerId());
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

        ConsumerPrivacyPolicyResultVO consumerPrivacyPolicyResultVO = sdm0307Service.getConsumerPrivacyPolicyResultVO(owner.getConsumerId());
        result.setPolicyResultFlag(consumerPrivacyPolicyResultVO.getAgreementResult());

        CmmConsumerVO user = sdm0307Service.getCmmConsumerVO(salesOrderVO.getUserConsumerId());
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
    
            ConsumerPrivateDetailVO userDetail = sdm0307Service.getConsumerPrivateDetailVO(user.getConsumerRetrieve());
            result.setUserMobile(userDetail.getMobilePhone());
        }

        result.setUseType(RegistrationDocumentFeatrueCategory.USETYPE001.getCodeDbid());
        result.setOwnerType(RegistrationDocumentFeatrueCategory.OWNERTYPE003.getCodeDbid());
        result.setPurchaseType(PurchaseType.NEW.getCodeDbid());

        return result;
    }

    public SDM030701BO getMotorcycleInfo(SDM030701Form form) {

        SDM030701BO formData = form.getFormData();
        SDM030701BO result = new SDM030701BO();
        SerializedProductVO serializedProductVO = sdm0307Service.getSerializedProductVO(form.getSiteId(), formData.getFrameNo());
        if (null == serializedProductVO) {
            return result;
        }
        result.setEvFlag(serializedProductVO.getEvFlag());
        result.setFrameNo(serializedProductVO.getFrameNo());
        result.setEngineNo(serializedProductVO.getEngineNo());
        result.setSerializedProductId(serializedProductVO.getSerializedProductId());

        MstProductVO mstProductVO = sdm0307Service.getMstProductVO(serializedProductVO.getProductId());
        if (null != mstProductVO) {
            result.setProductId(mstProductVO.getProductId());
            result.setModelCd(mstProductVO.getProductCd());
            result.setModelNm(mstProductVO.getSalesDescription());
            result.setColorNm(mstProductVO.getColorNm());
            result.setModelType(mstProductVO.getModelType());
            result.setDisplacement(mstProductVO.getDisplacement());
            result.setModel(mstProductVO.getProductCd() + CommonConstants.CHAR_SPACE + mstProductVO.getSalesDescription());

            ProductTaxVO productTaxVO = sdm0307Service.getProductTaxVO(mstProductVO.getProductId());
            BigDecimal taxRate = new BigDecimal("1.1");
            if (null != productTaxVO) {
                taxRate = new BigDecimal("1.08");
            }
            BigDecimal stdRetailPrice = null == mstProductVO.getStdRetailPrice() ? BigDecimal.ZERO : mstProductVO.getStdRetailPrice();
            result.setSellingPrice(stdRetailPrice.multiply(taxRate));
        }

        if (CommonConstants.CHAR_Y.equals(result.getEvFlag())) {
            BatteryVO batteryVO1 = sdm0307Service.getBatteryVO(serializedProductVO.getSerializedProductId(), BatteryType.TYPE1.getCodeDbid());
            if (null != batteryVO1) {
                result.setBatteryNo1(batteryVO1.getBatteryNo());
                result.setBatteryId1(batteryVO1.getBatteryId());
            }
            BatteryVO batteryVO2 = sdm0307Service.getBatteryVO(serializedProductVO.getSerializedProductId(), BatteryType.TYPE2.getCodeDbid());
            if (null != batteryVO2) {
                result.setBatteryNo2(batteryVO2.getBatteryNo());
                result.setBatteryId2(batteryVO2.getBatteryId());
            }
        }

        return result;
    }

    public void check(SDM030701Form form) {

        SDM030701BO formData = form.getFormData();

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

        if (formData.getSellingPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00201", new String[] { CodedMessageUtils.getMessage("label.sellingPrice"), CommonConstants.CHAR_ZERO }));
        }

        if (formData.getActualAmt().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00201", new String[] { CodedMessageUtils.getMessage("label.amount"), CommonConstants.CHAR_ZERO }));
        }

        SDCommonCheckBO checkBO = new SDCommonCheckBO();
        checkBO.setSiteId(form.getSiteId());
        checkBO.setPointCd(formData.getPointCd());
        checkBO.setFrameNo(formData.getFrameNo());
        checkBO.setModelCd(formData.getModelCd());
        checkBO.setSalesDate(formData.getSalesDate());
        checkBO.setModelId(formData.getProductId());
        checkBO.setPointId(formData.getPointId());

        sdm0307Service.checkSerializedProductCorrelation(checkBO);
        if (CommonConstants.CHAR_Y.equals(formData.getEvFlag())) {
            sdm0307Service.checkBatteryStatus(formData.getBatteryNo1(), formData.getBatteryNo2(), formData.getBatteryId1(), formData.getBatteryId2());
        }

    }

    public SDM030701BO save(SDM030701Form form) {

        return sdm0307Service.save(form);
    }

    public SDM030701BO delivery(SDM030701Form form) {

        return sdm0307Service.delivery(form);
    }
}
