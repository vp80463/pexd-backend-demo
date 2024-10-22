package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ030101BO extends BaseBO {

    private static final long serialVersionUID = 1L;
    private String receiptNo;
    private String firstCaseNo;
    private String parts;
    private String partsName;
    private Long partsId;
    private String orderNo;
    private String ymvnInvoiceNo;
    private String receiptDate;
    private BigDecimal receiptQty;
    private BigDecimal receiptPrice;
    private BigDecimal total;
    private String transactionTypeCd;
    private String transactionType;
    private String timeProcessing;
    private String completedDate;
    private String completedTime;
    private String instructionDate;
    private String instructionTime;
    private String registerLocation;
    private String locationType;
    private BigDecimal registerQty;
    private String registerDate;
    private Long storingLineId;
    private Long receiptSlipId;

    private String caseNo;
    private String partsNo;
    private String locationCd;
    private String lineNo;
    private String pointAbbr;
    private BigDecimal boQty;
    private String mainSign;
    private String supplier;
    private String fromPoint;
    private BigDecimal locationStockQty;
    private BigDecimal instrQty;
    private BigDecimal actualQty;
    private BigDecimal actualLoc;
}
