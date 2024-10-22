package com.a1stream.domain.bo.batch;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsPurchaseOrderModelBO {

    private String dealerCode;
    private String boCancelSign;
    private String orderDate;
    private String orderType;
    private String orderNo;
    private String orderLineNo;
    private String partNo;
    private BigDecimal orderQty;
    private String createdDate;
    private String consignee;
    private String consigneeName;
    private String addressInfo1;
    private String addressInfo2;
    private String telNo;
    private String lineEnd;
}
