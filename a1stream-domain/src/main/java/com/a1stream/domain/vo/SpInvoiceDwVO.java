package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class SpInvoiceDwVO {

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

    private String invoiceNo;

    private String invoiceType;

    private String productCd;

    private String productNm;

    private String middleGroupCd;

    private String middleGroupNm;

    private String largeGroupCd;

    private String largeGroupNm;

    private String productClassification;

    private String abcType;

    private BigDecimal invoiceQty = BigDecimal.ZERO;

    private BigDecimal invoicePrice = BigDecimal.ZERO;

    private BigDecimal invoiceAmt = BigDecimal.ZERO;

    private BigDecimal invoiceCost = BigDecimal.ZERO;

    private Integer shipmentLine = CommonConstants.INTEGER_ZERO;

    private Integer returnLine = CommonConstants.INTEGER_ZERO;

    private String salesOrderNo;

    private String orderSourceType;

    private String orderToType;

    private String createDatetime;

    private BigDecimal invoicePriceNotVat = BigDecimal.ZERO;

    private BigDecimal invoiceAmtNotVat = BigDecimal.ZERO;
}
