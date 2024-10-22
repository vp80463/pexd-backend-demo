package com.a1stream.domain.form.service;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010702Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String siteId;
    private String plateNo;
    private Long serializedProductId;
    private String modelCd;
    private String modelNm;
    private String model;
    private Long productId;
    private Long pointId;
    private String orderNo;
    private String consumerNm;
    private Long consumerId;
    private String mobilePhone;
    private String reservationDate;
    private String reservationTime;
    private String efTime;
    private String serviceBooking;
    private String reservationStatus;
    private String reservationMethod;
    private String memo;
    private Long serviceScheduleId;
    private String ServiceOrderSettledFlag;
    private boolean disEditFlag1;
    private boolean disEditFlag2;
    private boolean disEditFlag3;
}
