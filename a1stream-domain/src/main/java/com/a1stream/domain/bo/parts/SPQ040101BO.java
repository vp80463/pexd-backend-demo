package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ040101BO extends BaseBO {

    private static final long serialVersionUID = 1L;
    private String orderNo;
    private String purchaseOrderId;
    private String orderType;
    private String method;
    private String salesOrderNo;
    private String pointCd;
    private String orderDate;
    private String partsNo;
    private String partsNm;
    private String partsId;
    private BigDecimal orderQty;
    private BigDecimal purchasePrice;
    private BigDecimal orderAmount;
    private BigDecimal onPurchaseQty;
    private BigDecimal receiptQty;
    private BigDecimal registerQty;
    private BigDecimal cancelQty;


}