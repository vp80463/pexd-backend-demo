package com.a1stream.common.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class SalesReturnDetailBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long productId;

    private String productCd;

    private String productNm;

    private Long locationId;

    private String locationCd;

    private BigDecimal returnPrice;

    private BigDecimal returnQty;

    private BigDecimal returnAmount;

    private BigDecimal returnPriceNotVat;

    private BigDecimal cost;

    private BigDecimal taxRate;

    private Long invoiceItemId;

    private Long relatedSoItemId;

    private String salesOrderNo;

    private String orderDate;

    private String customerOrderNo;

    private String orderType;

    private String orderSourceType;

}