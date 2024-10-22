package com.a1stream.unit.facade;

import java.time.Year;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.PaymentMethodType;
import com.a1stream.common.constants.PJConstants.ConsumerType;
import com.a1stream.common.constants.PJConstants.GenderType;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.unit.SDM040103BO;
import com.a1stream.domain.form.unit.SDM040103Form;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.CmmRegistrationDocumentVO;
import com.a1stream.domain.vo.ConsumerPrivateDetailVO;
import com.a1stream.unit.service.SDM0401Service;
import com.alibaba.excel.util.StringUtils;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Component
public class SDM0401Facade {

    @Resource
    private SDM0401Service sdm0401Ser;

    @Resource
    private HelperFacade helperFac;

    public SDM040103BO findWarrantyCard(SDM040103Form form) {

        SDM040103BO result = new SDM040103BO();

        if (form.getRegistrationDocumentId() == null) { return result; }
        // 头部信息
        result = sdm0401Ser.getWarrantyCardBasicInfo(form.getRegistrationDocumentId());
        if(result != null) {

            Map<String, String> mstCodeMap = helperFac.getMstCodeInfoMap(ConsumerType.CODE_ID, GenderType.CODE_ID, PaymentMethodType.CODE_ID);
            // 车辆信息
            SDM040103BO motorInfo = sdm0401Ser.getMotorCycleInfo(result.getSerializedProductId());
            setMotorInfo2BO(result, motorInfo);
            // 车主信息
            if(result.getOwnerConsumerId() != null) {

                CmmConsumerVO ownerInfo = sdm0401Ser.getCmmConsumer(result.getOwnerConsumerId());
                ConsumerPrivateDetailVO ownerPrivateDetail = sdm0401Ser.getConsumerPrivateDetail(result.getOwnerConsumerId(), result.getSiteId());
                setOwnerInfo2BO(result, ownerInfo, ownerPrivateDetail, mstCodeMap);
            }
            // 用户信息
            if(result.getUserConsumerId() != null) {

                CmmConsumerVO userInfo = sdm0401Ser.getCmmConsumer(result.getUserConsumerId());
                ConsumerPrivateDetailVO userPrivateDetail = sdm0401Ser.getConsumerPrivateDetail(result.getUserConsumerId(), result.getSiteId());
                setUserInfo2BO(result, userInfo, userPrivateDetail, mstCodeMap);
            }
            result.setSameAsOwner(result.getOwnerConsumerId().compareTo(result.getUserConsumerId()) == 0? CommonConstants.CHAR_Y : CommonConstants.CHAR_N);
            // 省市名
            Map<Long, String> geomap = sdm0401Ser.findGeographyMap(result.getOwnerProvinceId(), result.getOwnerCityId(), result.getUserProvinceId(), result.getUserCityId());
            result.setOwnerProvince(geomap.get(result.getOwnerProvinceId()));
            result.setOwnerCity(geomap.get(result.getOwnerCityId()));
            result.setUserProvince(geomap.get(result.getUserProvinceId()));
            result.setUserCity(geomap.get(result.getUserCityId()));
        }

        return result;
    }

    public void updateWarrantyCard(SDM040103Form model) {

        CmmRegistrationDocumentVO cmmRegistDoc = sdm0401Ser.getCmmRegistrationDocumentById(model.getRegistrationDocumentId());

        cmmRegistDoc.setOwnerType(model.getOwnerTypeId());
        cmmRegistDoc.setUseType(model.getUserTypeId());
        cmmRegistDoc.setPurchaseType(model.getPurchaseTypeId());
        cmmRegistDoc.setPsvBrandNm(""+model.getPreviousBikeBrandId());
        cmmRegistDoc.setFamilyNum(model.getFamilyNum());
        cmmRegistDoc.setNumBike(model.getBikeNum());

        sdm0401Ser.updateWarrantyCard(cmmRegistDoc);
    }

    private void setMotorInfo2BO(SDM040103BO result, SDM040103BO motorInfo) {

        if (motorInfo != null) {
            result.setBarcode(motorInfo.getBarcode());
            result.setPlateNo(motorInfo.getPlateNo());
            result.setModelNm(motorInfo.getModelNm());
            result.setColorNm(motorInfo.getColorNm());
            result.setFrameNo(motorInfo.getFrameNo());
            result.setEngineNo(motorInfo.getEngineNo());
        }
    }

    private void setOwnerInfo2BO(SDM040103BO result, CmmConsumerVO ownerInfo, ConsumerPrivateDetailVO ownerPrivateDetail, Map<String, String> mstCodeMap) {

        if (ownerInfo != null) {
            result.setOwnerFullName(ownerInfo.getConsumerFullNm());
            result.setOwnerConsumerType(mstCodeMap.get(ownerInfo.getConsumerType()));
            result.setOwnerIdNo(ownerInfo.getIdNo());
            result.setOwnerRegistrationDate(ownerInfo.getRegistrationDate());
            result.setOwnerBusinessName(ownerInfo.getBusinessNm());
            result.setOwnerGender(mstCodeMap.get(ownerInfo.getGender()));
            String birthDay = ownerInfo.getBirthYear();
            if (StringUtils.isNotBlank(ownerInfo.getBirthDate())) {
                birthDay = ComUtil.changeFormat(ownerInfo.getBirthYear() + ownerInfo.getBirthDate());
            }
            result.setOwnerDateOfBirth(birthDay);
            result.setOwnerAge(Year.now().getValue()-Integer.parseInt(ownerInfo.getBirthYear()));
            result.setOwnerProvinceId(ownerInfo.getProvinceGeographyId());
            result.setOwnerCityId(ownerInfo.getCityGeographyId());
            result.setOwnerAddress(ownerInfo.getAddress());
            result.setOwnerAddress2(ownerInfo.getAddress2());
            result.setOwnerEmail(ownerInfo.getEmail());
            result.setOwnerEmail2(ownerInfo.getEmail2());
            result.setOwnerVipNo(ownerInfo.getVipNo());
            result.setOwnerOccupation(ownerInfo.getOccupation());
            result.setOwnerPaymentMethod(mstCodeMap.get(result.getOwnerPaymentMethodId()));
            result.setOwnerComment(ownerInfo.getComment());
        }
        if(ownerPrivateDetail != null){
            result.setOwnerLastName(ownerPrivateDetail.getLastNm());
            result.setOwnerMiddleName(ownerPrivateDetail.getMiddleNm());
            result.setOwnerFirstName(ownerPrivateDetail.getFirstNm());
            result.setOwnerMobilePhone(ownerPrivateDetail.getMobilePhone());
            result.setOwnerMobilePhone2(ownerPrivateDetail.getMobilePhone2());
            result.setOwnerMobilePhone3(ownerPrivateDetail.getMobilePhone3());
        }
    }

    private void setUserInfo2BO(SDM040103BO result, CmmConsumerVO userInfo, ConsumerPrivateDetailVO userPrivateDetail, Map<String, String> mstCodeMap) {

        if (userInfo != null) {

            result.setUserFullName(userInfo.getConsumerFullNm());
            result.setUserConsumerType(mstCodeMap.get(userInfo.getConsumerType()));
            result.setUserIdNo(userInfo.getIdNo());
            result.setUserRegistrationDate(userInfo.getRegistrationDate());
            result.setUserBusinessName(userInfo.getBusinessNm());
            result.setUserGender(mstCodeMap.get(userInfo.getGender()));
            String birthDay = userInfo.getBirthYear();
            if (StringUtils.isNotBlank(userInfo.getBirthDate())) {
                birthDay = ComUtil.changeFormat(userInfo.getBirthYear() + userInfo.getBirthDate());
            }
            result.setUserDateOfBirth(birthDay);
            result.setUserAge(Year.now().getValue()-Integer.parseInt(userInfo.getBirthYear()));
            result.setUserProvinceId(userInfo.getProvinceGeographyId());
            result.setUserCityId(userInfo.getCityGeographyId());
            result.setUserAddress(userInfo.getAddress());
            result.setUserAddress2(userInfo.getAddress2());
            result.setUserEmail(userInfo.getEmail());
            result.setUserEmail2(userInfo.getEmail2());
            result.setUserVipNo(userInfo.getVipNo());
            result.setUserOccupation(userInfo.getOccupation());
            result.setUserComment(userInfo.getComment());
        }
        if(userPrivateDetail != null){
            result.setUserLastName(userPrivateDetail.getLastNm());
            result.setUserMiddleName(userPrivateDetail.getMiddleNm());
            result.setUserFirstName(userPrivateDetail.getFirstNm());
            result.setUserMobilePhone(userPrivateDetail.getMobilePhone());
            result.setUserMobilePhone2(userPrivateDetail.getMobilePhone2());
            result.setUserMobilePhone3(userPrivateDetail.getMobilePhone3());
        }
    }
}
