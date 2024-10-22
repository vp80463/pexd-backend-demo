package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM013001BO implements Serializable {

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
    private Long relatedSalesOrderId;
    private String doFlag;
    private String boFlag;
    private String updateCounter;

    //motor info
    private String frameNo;
    private String model;
    private String modelCd;
    private String modelNm;
    private Long modelId;
    private String evFlag;
    private Long cmmSerializedProductId;

    //service info
    private String serviceCategoryId;
    private String serviceDemand;
    private String serviceTitle;
    private Long mechanicId;
    private String mechanicCd;
    private String mechanicNm;
    private String cashier;
    private String startTime;
    private String operationStart;
    private String operationFinish;

    private Long specialClaimId; //Campaign
    private String bulletinNo; //Campaign
    private String campaignNo; //Campaign

    private String mechanicComment;

    private BigDecimal paymentAmt;

    private List<SituationBO> situationList = new ArrayList<>();
    private List<JobDetailBO> jobList = new ArrayList<>();
    private List<PartDetailBO> partList = new ArrayList<>();
    private List<BatteryBO> batteryList = new ArrayList<>();
    private List<SummaryBO> summaryList = new ArrayList<>();
}
