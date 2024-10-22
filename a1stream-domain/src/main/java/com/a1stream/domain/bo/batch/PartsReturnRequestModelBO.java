package com.a1stream.domain.bo.batch;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsReturnRequestModelBO {

    private String dealerCode;
    private String consigneeCode;
    private String recommendType;
    private String partNo;
    private String yamahaInvoiceSeqNo;
    private String yamahaInvoiceNo;
    private BigDecimal requestedQty;
    private BigDecimal returnPrice;
}
