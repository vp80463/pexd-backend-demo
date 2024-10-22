package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Getter
@Setter
public class SPQ050801BO extends BaseBO {

    private static final long serialVersionUID = 1L;
    private String pointCd;
    private String pointNm;
    private BigDecimal beginningStockAmt;
    private BigDecimal receiptAmt;
    private BigDecimal salesCostAmt;
    private BigDecimal returnCostAmt;
    private BigDecimal disposalAmt;
    private BigDecimal adjustmentMinusAmt;
    private BigDecimal adjustmentPlusAmt;
    private BigDecimal transferOutAmt;
    private BigDecimal transferInAmt;
    private BigDecimal balanceOfCostAmt;
    private BigDecimal logicStockAmt;
    private BigDecimal stockMonthTarget;
    private BigDecimal stockMonth;
}