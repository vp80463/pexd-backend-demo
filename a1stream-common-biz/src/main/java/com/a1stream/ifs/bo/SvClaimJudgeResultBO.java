package com.a1stream.ifs.bo;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvClaimJudgeResultBO extends SvRequestIFBaseBO {

    private static final long serialVersionUID = 1L;

    private String ynspireReceiptNo;
    private String ynspireReceiptDate;
    private String judgeStatusCode;
    private String judgeStatusName;
    private BigDecimal paymentPartTotalAmount;
    private BigDecimal paymentLaborTotalAmount;
    private BigDecimal paymentTotalAmount;
    private String claimModifyComment;
    private String claimDeniedComment;
    private String claimAdujudicationMessage;
    private String couponAdujudicationMessage;
    private String dropDueDate;
    private String accountingMonth;
    private String claimType;

    private List<SvRequestJudgeResultParts> failedParts;
    private List<SvRequestJudgeResultJob> jobCodes;
}