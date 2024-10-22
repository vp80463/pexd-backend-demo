package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvRequestJudgeResultParts implements Serializable {

    private static final long serialVersionUID = 1L;

    private String exchangePartNo;
    private BigDecimal exchangePartQuantity;
    private String supersedingPartNo;
    private BigDecimal supplyPartQuantity;
    private BigDecimal paymentPartCost;
    private BigDecimal paymentPartAmount;
    private String partReceiveDate;
}