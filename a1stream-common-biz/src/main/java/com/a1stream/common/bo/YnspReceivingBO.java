/******************************************************************************/
/* SYSTEM     : DMS                                                           */
/*                                                                            */
/* SUBSYSTEM  : Xm03                                                          */
/******************************************************************************/
package com.a1stream.common.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class YnspReceivingBO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String requestId;

    private String sysOwnerCd;

    private String dealerCd;

    private String consigneeCd;

    private String shipmentNo;

    private String invoiceSeqNo;

    private String receiptDate;

    private String receiptTime;

    private String requestPassword;
}
