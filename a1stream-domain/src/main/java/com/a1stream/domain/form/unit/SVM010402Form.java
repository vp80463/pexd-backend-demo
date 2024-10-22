package com.a1stream.domain.form.unit;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010402Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long serializedProductId;

    private Long cmmSerializedProductId;

    private String plateNo;

    private String oldBatteryId1;

    private String oldBatteryId2;

    private String batteryId1;

    private String batteryId2;

    private String modelCode;

    private String salesStatus;

    private String soldDate;

    private String manufacturingDate;

    private String evFlag;

    private String frameNo;

    private String relationType;

    //consumerInfo
    private Long consumerId;

    private String firstNm;

    private String middleNm;

    private String lastNm;

    private String gender;

    private Long province;

    private Long city;

    private String mobilePhone;

    private String telephone;

    private String address1;

    private String address2;
}
