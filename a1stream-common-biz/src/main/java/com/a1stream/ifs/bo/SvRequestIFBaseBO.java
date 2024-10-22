package com.a1stream.ifs.bo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvRequestIFBaseBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String applicationPointCode;
    private String applicationNo;
    protected String applicationDealerCode;
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

    public String claimApplicationNo;
    public String repairCompletionDate;

    public String couponApplicationNo;
    public String couponServiceDate;

    public String originalBatteryId1;
    public String originalBatteryId2;
    public String newBatteryId1;
    public String newBatteryId2;
}