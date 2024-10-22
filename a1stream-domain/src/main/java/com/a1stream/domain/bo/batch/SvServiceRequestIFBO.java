package com.a1stream.domain.bo.batch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvServiceRequestIFBO implements Serializable{

    private static final long serialVersionUID = 1L;

    private String applicationPointCode;
    private String applicationDealerCode;
    private String applicationNo;
    private String authorizationNo;
    private String applicationDate;
    private String frameNo;
    private String salesDate;
    private String failureDate;
    private String serviceCompletionDate;
    private String mileage;
    private String symptomCode;
    private String conditionCode;
    private String problemDescription;
    private String causeDescription;
    private String repairDescription;
    private String dealerComment;
    private String primaryFailedPartNo;
    private String couponLevel;
    private String campaignNumber;
    private String claimType;
    private String claimApplicationNo;
    private String repairCompletionDate;
    private String couponApplicationNo;
    private String couponServiceDate;
    private String originalBatteryId1;
    private String originalBatteryId2;
    private String newBatteryId1;
    private String newBatteryId2;

    private List<SvServiceRequestJobIFBO> jobDetail = new ArrayList<>();
    private List<SvServiceRequestPartIFBO> partsDetail = new ArrayList<>();
}
