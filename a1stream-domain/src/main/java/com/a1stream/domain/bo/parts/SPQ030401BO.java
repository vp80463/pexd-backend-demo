package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ030401BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String partsNo;
    private String partsNm;
    private String pointCd;
    private String pointNm;
    private String transactionDate;
    private String transactionTime;
    private String transactionType;
    private String transactionTypeId;
    private String from;
    private String to;
    private BigDecimal inQty;
    private BigDecimal outQty;
    private BigDecimal afterQty;
    private BigDecimal cost;
    private BigDecimal averageCost;
    private String transactionNo;
}
