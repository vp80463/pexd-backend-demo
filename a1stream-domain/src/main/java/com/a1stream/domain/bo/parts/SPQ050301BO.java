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
public class SPQ050301BO extends BaseBO {

    private static final long serialVersionUID = 1L;
    private String pointCd;
    private String pointNm;
    private BigDecimal shipmentAmt;
    private BigDecimal shipmentCost;
    private BigDecimal returnAmt;
    private BigDecimal returnCost;
    private BigDecimal boAmt;
    private Integer soLine;
    private Integer allocateLine;
    private Integer boLine;
    private BigDecimal profit;
    private BigDecimal profitRate;
    private BigDecimal allocateRate;

}