package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseConsumerForm extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long consumerId;

    private String lastNm;

    private String middleNm;

    private String firstNm;

    private String mobilePhone;

    private String sns;

    private String gender;

    private String birthYear;

    private String birthDate;

    private String privacyResult;

    private Long province;

    private Long district;

    private String address;

    private String email;

    private String taxCode;

    private String registDate;

    private String comment;

    private String businessNm;

    private String address2;

    private String telephone;

    private String email2;

    private String occupation;

    private String consumerType;
}
