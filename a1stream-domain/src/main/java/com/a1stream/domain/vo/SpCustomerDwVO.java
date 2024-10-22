package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class SpCustomerDwVO {

    private String siteId;

    private String accountMonth;

    private String targetYear;

    private String targetMonth;

    private String targetDay;

    private String facilityCd;

    private String facilityNm;

    private String customerCd;

    private String customerNm;

    private String consumerNm;

    private String productClassification;

    private Integer soLine = CommonConstants.INTEGER_ZERO;

    private Integer allocatedLine = CommonConstants.INTEGER_ZERO;

    private Integer boLine = CommonConstants.INTEGER_ZERO;

    private Integer soCancelLine = CommonConstants.INTEGER_ZERO;

    private Integer shipmentLine = CommonConstants.INTEGER_ZERO;

    private Integer returnLine = CommonConstants.INTEGER_ZERO;

    private BigDecimal soAmt = BigDecimal.ZERO;

    private BigDecimal allocatedAmt = BigDecimal.ZERO;

    private BigDecimal boAmt = BigDecimal.ZERO;

    private BigDecimal soCancelAmt = BigDecimal.ZERO;

    private BigDecimal invoiceAmt = BigDecimal.ZERO;

    private BigDecimal invoiceCost = BigDecimal.ZERO;

    private String createDatetime;
}
