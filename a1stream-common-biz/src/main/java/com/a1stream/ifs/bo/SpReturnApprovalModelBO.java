package com.a1stream.ifs.bo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpReturnApprovalModelBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dealerCd;
    private String consigneeCd;
    private String recommendType;
    private String partNo;
    private String yamahaInvoiceSeqNo;
    private String yamahaExternalInvoiceNo;
    private String approvedQty;
    private String price;
}