package com.a1stream.domain.bo.batch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsReceiveReportModelBO {

    private String requestId;
    private String sysOwnerCd;
    private String dealerCd;
    private String consigneeCd;
    private String shipmentNo;
    private String invoiceSeqNo;
    private String receiptDate;
    private String receiptTime;
    private String requestPassword;
}
