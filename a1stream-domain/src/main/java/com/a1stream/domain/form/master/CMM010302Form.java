package com.a1stream.domain.form.master;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.master.CMM010302BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM010302Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String siteId;
    private Long consumerId;
    // General Information
    private String consumerType;
    private String lastNm;
    private String middleNm;
    private String firstNm;
    private String policyResultFlag;
    private String policyFileName;
    private String gender;
    private String mobilePhone;
    private String mobile2;
    private String mobile3;
    private String birthday;
    private String age;
    private String regDate;
    private String vipNo;
    private Long province;
    private Long cityId;
    private String addr1;
    private String addr2;
    private String email1;
    private String email2;
    private String occupation;
    private String comment;
    private String interestModel;
    private String currentBikeBrand;
    private String currentBikePurchase;
    private String registrationReason;

    // ID No. & Contact
    private String consumerIdentification;
    private String idNo;
    private String visaNo;
    private String faxNo;
    private String postCd;
    private String telephone;

    private List<CMM010302BO> motorcycleInfoList;
    private List<CMM010302BO> serviceDetailList;
}