package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ050802BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String date;

    private BigDecimal beginningStockAmount;

    private BigDecimal receiptAmount;

    private BigDecimal salesCost;

    private BigDecimal returnCost;

    private BigDecimal disposalCost;

    private BigDecimal adjustmentNegative;

    private BigDecimal adjustmentPositive;

    private BigDecimal transferAmountOut;

    private BigDecimal transferAmountIn;

    private BigDecimal balanceOfCost;

    private BigDecimal seq;
}
