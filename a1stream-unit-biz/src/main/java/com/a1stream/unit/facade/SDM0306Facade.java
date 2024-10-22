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
import com.a1stream.domain.bo.unit.SDM030601BO;
import com.a1stream.domain.form.unit.SDM030601Form;
import com.a1stream.domain.vo.BatteryVO;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.CmmEmployeeInstructionVO;
import com.a1stream.domain.vo.ConsumerPrivacyPolicyResultVO;
import com.a1stream.domain.vo.ConsumerPrivateDetailVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductTaxVO;
import com.a1stream.domain.vo.SalesOrderItemVO;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.a1stream.unit.service.SDM0306Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: Special Order Entry 
*
* mid2303
* 2024年10月8日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/10/08   Ruan Hansheng   New
*/
@Component
public class SDM0306Facade {

    @Resource
    private SDM0306Service sdm0306Service;

    public SDM030601BO getInitResult(SDM030601Form form) {

        SDM030601BO formData = form.getFormData();
        SDM030601BO result = new SDM030601BO();

        CmmEmployeeInstructionVO cmmEmployeeInstructionVO = sdm0306Service.getCmmEmployeeInstructionVO(formData.getCmmEmployeeInstructionId());
        result.setEmployee(cmmEmployeeInstructionVO.getEmployeeCd() + CommonConstants.CHAR_SPACE + cmmEmployeeInstructionVO.getEmployeeNm());
        result.setEmployeeCd(cmmEmployeeInstructionVO.getEmployeeCd());
        result.setEmployeeNm(cmmEmployeeInstructionVO.getEmployeeNm());
        result.setTaxRate(cmmEmployeeInstructionVO.getTaxRate());
        result.setEmployeeDiscount(cmmEmployeeInstructionVO.getEmployeeDiscount());
        formData.setSalesOrderId(cmmEmployeeInstructionVO.getLocalOrderId());

        String sysDate = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD);

        if (ObjectUtils.isEmpty(formData.getSalesOrderId())) {
            result.setSalesDate(sysDate);
            result.setConsumerType(ConsumerType.BIKEPURCHASECUSTOMER.getCodeDbid());
            result.setUConsumerType(ConsumerType.BIKEPURCHASECUSTOMER.getCodeDbid());
            result.setOccupation(Occupation.NA);
            result.setPdiDate(sysDate);
            result.setPaymentMethod(PaymentMethodType.CASH);
            result.setPoint(formData.getFacilityCd() + CommonConstants.CHAR_SPACE + formData.getFacilityNm());
            result.setPointId(formData.getFacilityId());
            result.setPointCd(formData.getFacilityCd());
            result.setPointNm(formData.getFacilityNm());
            result.setSalesPicNm(form.getPersonNm());
            result.setSalesPic(form.getPersonId());
            result.setModel(cmmEmployeeInstructionVO.getModelCd() + CommonConstants.CHAR_SPACE + cmmEmployeeInstructionVO.getModelNm());
            result.setModelCd(cmmEmployeeInstructionVO.getModelCd());
            result.setModelNm(cmmEmployeeInstructionVO.getModelNm());
            result.setFrameNo(cmmEmployeeInstructionVO.getFrameNo());
            result.setEngineNo(cmmEmployeeInstructionVO.getEngineNo());
            result.setColorNm(cmmEmployeeInstructionVO.getColorNm());
            result.setOwnerCusTaxCode(cmmEmployeeInstructionVO.getCustaxCd());
            result.setProductId(cmmEmployeeInstructionVO.getCmmProductId());
            result.setSerializedProductId(cmmEmployeeInstructionVO.getSerializedProductId());
            if (null != cmmEmployeeInstructionVO.getSerializedProductId()) {
                BatteryVO batteryVO1 = sdm0306Service.getBatteryVO(cmmEmployeeInstructionVO.getSerializedProductId(), BatteryType.TYPE1.getCodeDbid());
                if (null != batteryVO1) {
                    result.setBatteryNo1(batteryVO1.getBatteryNo());
                    result.setBatteryId1(batteryVO1.getBatteryId());
                }
                BatteryVO batteryVO2 = sdm0306Service.getBatteryVO(cmmEmployeeInstructionVO.getSerializedProductId(), BatteryType.TYPE2.getCodeDbid());
                if (null != batteryVO2) {
                    result.setBatteryNo2(batteryVO2.getBatteryNo());
                    result.setBatteryId2(batteryVO2.getBatteryId());
                }
            }

            result.setOwnerConsumerId(cmmEmployeeInstructionVO.getConsumerId());
            result.setOwnerLastNm(cmmEmployeeInstructionVO.getLastNm());
            result.setOwnerMiddleNm(cmmEmployeeInstructionVO.getMiddleNm());
            result.setOwnerFirstNm(cmmEmployeeInstructionVO.getFirstNm());
            result.setOwnerMobile(cmmEmployeeInstructionVO.getMobilePhone());
            result.setOwnerSns(cmmEmployeeInstructionVO.getSns());
            result.setOwnerGender(cmmEmployeeInstructionVO.getGender());
            result.setOwnerBirthYear(cmmEmployeeInstructionVO.getBirthYear());
            result.setOwnerBirthDate(cmmEmployeeInstructionVO.getBirthDate());
            result.setOwnerProvince(cmmEmployeeInstructionVO.getProvinceGeographyId());
            result.setOwnerDistrict(cmmEmployeeInstructionVO.getCityGeographyId());
            result.setOwnerAddress(cmmEmployeeInstructionVO.getAddress());
            result.setOwnerEmail(cmmEmployeeInstructionVO.getEmail());
            
            LocalDate currentDate = LocalDate.now();
            int currentYear = currentDate.getYear();
            int ownerAge = currentYear - Integer.parseInt(cmmEmployeeInstructionVO.getBirthYear());
            result.setOwnerAge(String.valueOf(ownerAge));
            
            ConsumerPrivacyPolicyResultVO consumerPrivacyPolicyResultVO = sdm0306Service.getConsumerPrivacyPolicyResultVO(cmmEmployeeInstructionVO.getConsumerId());
            result.setPolicyResultFlag(consumerPrivacyPolicyResultVO.getAgreementResult());

            result.setSellingPrice(BigDecimal.ZERO);
            result.setActualAmt(BigDecimal.ZERO);

            return result;
        }

        SalesOrderVO salesOrderVO = sdm0306Service.getSalesOrderVO(formData.getSalesOrderId());
        result.setSalesOrderId(formData.getSalesOrderId());
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
        result.setSalesDate(salesOrderVO.getOrderDate());

        MstFacilityVO mstFacilityVO = sdm0306Service.getMstFacilityVO(salesOrderVO.getFacilityId());
        result.setMultiAddressFlag(mstFacilityVO.getMultiAddressFlag());
        result.setPoint(mstFacilityVO.getFacilityCd() + CommonConstants.CHAR_SPACE + mstFacilityVO.getFacilityNm());
        result.setPointId(mstFacilityVO.getFacilityId());
        result.setPointCd(mstFacilityVO.getFacilityCd());
        result.setPointNm(mstFacilityVO.getFacilityNm());

        List<SalesOrderItemVO> salesOrderItemVOList = sdm0306Service.getSalesOrderItemVOList(formData.getSalesOrderId());
        SalesOrderItemVO salesOrderItemVO = salesOrderItemVOList.get(0);
        result.setProductId(salesOrderItemVO.getProductId());
        result.setDiscountAmt(salesOrderItemVO.getDiscountAmt());
        result.setSellingPrice(salesOrderItemVO.getSellingPrice());
        result.setSellingPriceNotVat(salesOrderItemVO.getSellingPriceNotVat());
        result.setSpecialPrice(salesOrderItemVO.getSpecialPrice());
        result.setActualAmt(salesOrderItemVO.getActualAmt());
        result.setActualAmtNotVat(salesOrderItemVO.getActualAmtNotVat());

        if (CommonConstants.CHAR_Y.equals(result.getEvFlag())) {
            BatteryVO batteryVO1 = sdm0306Service.getBatteryVO(salesOrderVO.getSerializedProductId(), BatteryType.TYPE1.getCodeDbid());
            if (null != batteryVO1) {
                result.setBatteryNo1(batteryVO1.getBatteryNo());
                result.setBatteryId1(batteryVO1.getBatteryId());
            }
    
            BatteryVO batteryVO2 = sdm0306Service.getBatteryVO(salesOrderVO.getSerializedProductId(), BatteryType.TYPE2.getCodeDbid());
            if (null != batteryVO2) {
                result.setBatteryNo2(batteryVO2.getBatteryNo());
                result.setBatteryId2(batteryVO2.getBatteryId());
            }
        }

        CmmConsumerVO owner = sdm0306Service.getCmmConsumerVO(salesOrderVO.getCmmConsumerId());
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

        ConsumerPrivacyPolicyResultVO consumerPrivacyPolicyResultVO = sdm0306Service.getConsumerPrivacyPolicyResultVO(owner.getConsumerId());
        result.setPolicyResultFlag(consumerPrivacyPolicyResultVO.getAgreementResult());

        CmmConsumerVO user = sdm0306Service.getCmmConsumerVO(salesOrderVO.getUserConsumerId());
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
    
            ConsumerPrivateDetailVO userDetail = sdm0306Service.getConsumerPrivateDetailVO(user.getConsumerRetrieve());
            result.setUserMobile(userDetail.getMobilePhone());
        }

        result.setUseType(RegistrationDocumentFeatrueCategory.USETYPE001.getCodeDbid());
        result.setOwnerType(RegistrationDocumentFeatrueCategory.OWNERTYPE003.getCodeDbid());
        result.setPurchaseType(PurchaseType.NEW.getCodeDbid());

        return result;
    }

    public SDM030601BO getMotorcycleInfo(SDM030601Form form) {

        SDM030601BO formData = form.getFormData();
        SDM030601BO result = new SDM030601BO();
        SerializedProductVO serializedProductVO = sdm0306Service.getSerializedProductVO(form.getSiteId(), formData.getFrameNo());
        if (null == serializedProductVO) {
            return result;
        }
        result.setEvFlag(serializedProductVO.getEvFlag());
        result.setFrameNo(serializedProductVO.getFrameNo());
        result.setEngineNo(serializedProductVO.getEngineNo());
        result.setSerializedProductId(serializedProductVO.getSerializedProductId());

        MstProductVO mstProductVO = sdm0306Service.getMstProductVO(serializedProductVO.getProductId());
        if (null != mstProductVO) {
            result.setProductId(mstProductVO.getProductId());
            result.setModelCd(mstProductVO.getProductCd());
            result.setModelNm(mstProductVO.getSalesDescription());
            result.setColorNm(mstProductVO.getColorNm());
            result.setModelType(mstProductVO.getModelType());
            result.setDisplacement(mstProductVO.getDisplacement());
            result.setModel(mstProductVO.getProductCd() + CommonConstants.CHAR_SPACE + mstProductVO.getSalesDescription());

            ProductTaxVO productTaxVO = sdm0306Service.getProductTaxVO(mstProductVO.getProductId());
            BigDecimal taxRate = new BigDecimal("1.1");
            if (null != productTaxVO) {
                taxRate = new BigDecimal("1.08");
            }
            BigDecimal stdRetailPrice = null == mstProductVO.getStdRetailPrice() ? BigDecimal.ZERO : mstProductVO.getStdRetailPrice();
            result.setSellingPrice(stdRetailPrice.multiply(taxRate));
        }

        if (CommonConstants.CHAR_Y.equals(result.getEvFlag())) {
            BatteryVO batteryVO1 = sdm0306Service.getBatteryVO(serializedProductVO.getSerializedProductId(), BatteryType.TYPE1.getCodeDbid());
            if (null != batteryVO1) {
                result.setBatteryNo1(batteryVO1.getBatteryNo());
                result.setBatteryId1(batteryVO1.getBatteryId());
            }
            BatteryVO batteryVO2 = sdm0306Service.getBatteryVO(serializedProductVO.getSerializedProductId(), BatteryType.TYPE2.getCodeDbid());
            if (null != batteryVO2) {
                result.setBatteryNo2(batteryVO2.getBatteryNo());
                result.setBatteryId2(batteryVO2.getBatteryId());
            }
        }

        return result;
    }

    public void check(SDM030601Form form) {

        SDM030601BO formData = form.getFormData();

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

        sdm0306Service.checkSerializedProductCorrelation(checkBO);
        if (CommonConstants.CHAR_Y.equals(formData.getEvFlag())) {
            sdm0306Service.checkBatteryStatus(formData.getBatteryNo1(), formData.getBatteryNo2(), formData.getBatteryId1(), formData.getBatteryId2());
        }

    }

    public SDM030601BO save(SDM030601Form form) {

        return sdm0306Service.save(form);
    }

    public SDM030601BO delivery(SDM030601Form form) {

        return sdm0306Service.delivery(form);
    }
}
