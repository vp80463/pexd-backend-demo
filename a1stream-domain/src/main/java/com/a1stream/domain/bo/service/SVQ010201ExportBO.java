package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVQ010201ExportBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderDate;
    private String orderNo;
    private String orderStatus;
    private LocalDateTime startTime;
    private String startTimeStr;
    private LocalDateTime finishTime;
    private String finishTimeStr;
    private String consumerCd;
    private String consumerNm;
    private String lastNm;
    private String middleNm;
    private String firstNm;
    private String policyResult;
    private String address;
    private String mobilePhone;
    private String plateNo;
    private String frameNo;
    private String engineNo;
    private String modelCd;
    private String modelNm;
    private String color;
    private String soldDate;
    private String serviceCategory;
    private String serviceDemand;
    private BigDecimal mileage;
    private String receptionName;
    private String mechanicName;
    private String workTime;
    private BigDecimal serviceAmount;
    private BigDecimal partsAmount;
    private String brand;
    private String settleDate;

    public static SVQ010201ExportBO createtTotalRow(BigDecimal serviceAmountTotal, BigDecimal partsAmountTotal, String workTime) {
        SVQ010201ExportBO result = new SVQ010201ExportBO();
        result.setMechanicName("Total:");
        result.setServiceAmount(serviceAmountTotal);
        result.setPartsAmount(partsAmountTotal);
        result.setWorkTime(workTime);

        return result;
    }
}
