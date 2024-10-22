package com.a1stream.domain.bo.master;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM071601BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String date;

    private String type;

    private String interfaceType;

    private String invoiceNo;

    private String orderNo;

    private String status;

    private Integer sendTimes;

    private String returnMessage;

    private Long relatedInvoiceId;

    private String statusChangeFlag;

}
