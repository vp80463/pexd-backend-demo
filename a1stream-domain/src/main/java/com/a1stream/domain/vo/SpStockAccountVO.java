package com.a1stream.domain.vo;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class SpStockAccountVO {

    private String siteId;

    private String accountMonth;

    private String targetYear;

    private String targetMonth;

    private String targetDay;

    private String facilityCd;

    private String facilityNm;

    private String largeGroupCd;

    private String largeGroupNm;

    private String productClassification;

    private BigDecimal beginMonthStockAmt = BigDecimal.ZERO;

    private BigDecimal receiptAmt = BigDecimal.ZERO;

    private BigDecimal salesCostAmt = BigDecimal.ZERO;

    private BigDecimal returnCostAmt = BigDecimal.ZERO;

    private BigDecimal adjustPlusAmt = BigDecimal.ZERO;

    private BigDecimal adjustMinusAmt = BigDecimal.ZERO;

    private BigDecimal disposalAmt = BigDecimal.ZERO;

    private BigDecimal transferInAmt = BigDecimal.ZERO;

    private BigDecimal transferOutAmt = BigDecimal.ZERO;

    private BigDecimal balanceCostAmt = BigDecimal.ZERO;

    private String createDatetime;
}
