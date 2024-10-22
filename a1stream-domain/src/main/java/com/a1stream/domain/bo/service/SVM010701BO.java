package com.a1stream.domain.bo.service;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010701BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String reservationSts;
    private String reservationDate;
    private String reservationTime;
    private String reservationNo;
    private String plateNo;
    private String model;
    private String consumer;
    private String mobilePhone;
    private String serviceBooking;
    private String memo;
    private String reservationMethod;
    private Long serviceScheduleId;
    private Long serviceOrderId;
    private Long consumerId;
    private Long serializedProductId;
    private String orderNo;
    private String serviceStatus;
    private String pic;
    private boolean disViewFlag;
}