package com.a1stream.common.bo;

import java.io.Serial;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDocument implements Serializable {

    @Serial
    private static final long serialVersionUID = -5242623354746874888L;

    private String frameNo;

    private String registrationDealerCode;

    private String salesDate;

    private String dateType;

    private String useType;

    private String ownerType;

    private String ownerNameFirst;

    private String ownerNameMiddle;

    private String ownerNameLast;

    private String businessNameMiddle;

    private String businessNameFirst;

    private String businessNameLast;

    private String address1;

    private String address2;

    private Long provinceCode;

    private String provinceName;

    private Long city;

    private String telephoneNumber;

    private String cellPhoneNumber;

    private String faxNumber;

    private String sex;

    private String birthday;

    private String occupationTp;

    private String emailPrimary;

    private String emailSecondary;

    private String comment;

    private String registrationPointCode;

    private String batteryId1;

    private String batteryId2;

    private String batteryCode1;

    private String batteryCode2;

    private String categoryType;

    private String pdiDate;

    private String justificationComment;

}
