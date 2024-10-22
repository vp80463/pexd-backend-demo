package com.a1stream.ifs.bo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SvRegisterDocAbstractBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String frameNo;
    private String registrationDealerCode;
    private String registrationNo;
    private String salesDate;
    private String useType;
    private String ownerType;
    private String ownerNameFirst;
    private String ownerNameLast;
    private String businessName;
    private String businessNameFirst;
    private String businessNameLast;
    private String address1;
    private String address2;
    private String provinceCode;
    private String provinceName;
    private String city;
    private String telephoneNumber;
    private String cellphoneNumber;
    private String faxNumber;
    private String sex;
    private String birthday;
    private String occupationTp;
    private String emailPrimary;
    private String emailSecondary;
    private String ownerContactFlg;
    private String comment;
    private String registrationPointCode;

    private String batteryId1="";
    private String batteryId2="";
    private String batteryCode1="";
    private String batteryCode2="";
    private String categoryType="";

    public String getDealerCode() {
        return this.getRegistrationDealerCode();
    }
}