package com.a1stream.ifs.bo;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpRecommendationReturnModelBO {

    private String dealerCode;

    private String pointCode;

    private String recommendType;

    private String partsNo;

    private String invoiceSeqNo;

    private String externalInvoiceNo;

    private BigDecimal recommandQty;

    private BigDecimal price;

    private String expireDate;
}
