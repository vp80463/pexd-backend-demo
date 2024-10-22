package com.a1stream.domain.bo.batch;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvServicePaymentIFBO implements Serializable{

    private static final long serialVersionUID = 1L;

    private String paymentControlNo;
    private String paymentDealerCode;
    private String dealerReceiptDate;
    private String vatCode;
    private String invoiceNo;
    private String invoiceDate;
    private String serialNo;
    private String bulletinNo;
}
