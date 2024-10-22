package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.a1stream.common.constants.CommonConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010201BO implements Serializable {

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
    private String plateNo;
    private String frameNo;
    private String soldDate;
    private String model;
    private String modelCd;
    private String modelNm;
    private Long modelId;
    private Long modelTypeId;
    private String evFlag;
    private Long cmmSerializedProductId;
    private String ownerType;
    private String useType;
    private Long consumerId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobilephone;
    private String email;
    private String policyResultFlag;
    private String createForEmployeeFlag = CommonConstants.CHAR_N;
    private String employeeCd;
    private String relationShipId;
    private String ticketNo;

    //motor Info hidden
    private String policyFileName;

    //service info
    private BigDecimal mileage;
    private String serviceCategoryId;
    private String serviceDemand;
    private Long serviceDemandId;
    private String serviceTitle;
    private Long mechanicId;
    private String mechanicCd;
    private String mechanicNm;
    private Long welcomeStaffId;
    private String welcomeStaffCd;
    private String welcomeStaffNm;
    private String editor;
    private String cashier;
    private String startTime;
    private String operationStart;
    private String operationFinish;

    private Long specialClaimId; //Campaign
    private String bulletinNo; //Campaign
    private String jobCd; //Free Service default JobCd
    private Long servicePackageId;
    private String servicePackageCd;
    private String servicePackageNm;

    private String mechanicComment;

    private BigDecimal depositAmt;
    private String paymentMethodId;

    private List<SituationBO> situationList = new ArrayList<>();
    private List<JobDetailBO> jobList = new ArrayList<>();
    private List<PartDetailBO> partList = new ArrayList<>();
    private List<BatteryBO> batteryList = new ArrayList<>();
    private List<SVM010201HistoryBO> historyList = new ArrayList<>();
    private List<SummaryBO> summaryList = new ArrayList<>();
}
