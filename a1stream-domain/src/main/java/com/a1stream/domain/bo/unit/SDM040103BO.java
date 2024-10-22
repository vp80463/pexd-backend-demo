package com.a1stream.domain.bo.unit;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Getter
@Setter
public class SDM040103BO implements Serializable {

    private static final long serialVersionUID = 1L;

    // header
    private String entryFacilityNm;
    private String picNm;
    private String orderNo;
    private String deliveryDate;
    private String salesDate;
    private Long serializedProductId;
    private Long salesOrderId;
    private String siteId;
    private Long ownerConsumerId;
    private Long userConsumerId;
    private String ownerTypeId;
    private String userTypeId;
    private String ownerPaymentMethodId;
    private Long registrationDocumentId;

    // motocycle
    private String barcode;
    private String plateNo;
    private String modelNm;
    private String colorNm;
    private String frameNo;
    private String engineNo;

    // ownerInfo
    private String ownerFullName;
    private String ownerConsumerType;
    private String ownerIdNo;
    private String ownerRegistrationDate;
    private String ownerBusinessName;
    private String ownerLastName;
    private String ownerMiddleName;
    private String ownerFirstName;
    private String ownerMobilePhone;
    private String ownerMobilePhone2;
    private String ownerMobilePhone3;
    private String ownerGender;
    private String ownerDateOfBirth;
    private Integer ownerAge;
    private Long ownerProvinceId;
    private Long ownerCityId;
    private String ownerProvince;
    private String ownerCity;
    private String ownerAddress;
    private String ownerAddress2;
    private String ownerEmail;
    private String ownerEmail2;
    private String ownerVipNo;
    private String ownerOccupation;
    private String ownerPaymentMethod;
    private String ownerComment;

    // userInfo
    private String sameAsOwner;
    private String userFullName;
    private String userConsumerType;
    private String userIdNo;
    private String userRegistrationDate;
    private String userLastName;
    private String userMiddleName;
    private String userFirstName;
    private String userBusinessName;
    private String userMobilePhone;
    private String userMobilePhone2;
    private String userMobilePhone3;
    private String userGender;
    private String userDateOfBirth;
    private Integer userAge;
    private Long userProvinceId;
    private Long userCityId;
    private String userProvince;
    private String userCity;
    private String userAddress;
    private String userAddress2;
    private String userEmail;
    private String userEmail2;
    private String userVipNo;
    private String userOccupation;
    private String userComment;
    private String purchaseTypeId;
    private String previousBikeBrandId;
    private String previousBikeName;
    private String previousBikeMtAt;
    private String familyNum;
    private String bikeNum;
}