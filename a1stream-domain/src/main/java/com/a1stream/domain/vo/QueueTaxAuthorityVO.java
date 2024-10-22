package com.a1stream.domain.vo;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class QueueTaxAuthorityVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long dbId;

    private Long relatedOrderId;

    private String relatedOrderNo;

    private Long relatedInvoiceId;

    private String relatedInvoiceNo;

    private String interfCode;

    private String invoiceDate;

    private String serverNm;

    private Integer sendTimes = CommonConstants.INTEGER_ZERO;

    private String status;

    private String statusMessage;

    private String fkeys;
}
