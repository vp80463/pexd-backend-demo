package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM012001BO implements Serializable {

    private static final long serialVersionUID = 1L;

    //basic Info
    private Long pointId;
    private String pointCd;
    private String pointNm;
    private String orderNo;
    private String orderDate;
    private String orderStatus;
    private String orderStatusId;

    //basic info hidden
    private Long serviceOrderId;
    private String doFlag;
    private String updateCounter;

    //motor info
    private String plateNo;
    private String frameNo;
    private String model;
    private String brandId;
    private Long modelTypeId;
    private Long consumerId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobilephone;
    private String email;
    private String policyResultFlag;

    //motor Info hidden
    private String policyFileName;

    //service info
    private BigDecimal mileage;
    private String serviceTitle;
    private Long mechanicId;
    private String mechanicCd;
    private String mechanicNm;
    private String cashier;
    private String startTime;
    private String operationStart;
    private String operationFinish;

    private String mechanicComment;

    private BigDecimal depositAmt;
    private String paymentMethodId;

    private List<SVM012001JobBO> jobList = new ArrayList<>();
    private List<SVM012001PartBO> partList = new ArrayList<>();
    private List<SummaryBO> summaryList = new ArrayList<>();
}
