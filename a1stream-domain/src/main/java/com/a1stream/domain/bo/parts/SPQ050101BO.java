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
public class SPQ050101BO extends BaseBO {

    private static final long serialVersionUID = 1L;
    private String pointCd;
    private String pointNm;
    private String middleGroupCd;
    private String middleGroupNm;
    private Integer soLine;
    private Integer allocateLine;
    private Integer boLine;
    private Integer cancelLine;
    private Integer shipmentLine;
    private Integer returnLine;
    private Integer poLine;
    private Integer poEoLine;
    private Integer poRoLine;
    private Integer poHoLine;
    private Integer poWoLine;
    private Integer receiptLine;

    private BigDecimal soAmt;
    private BigDecimal allocatedAmt;
    private BigDecimal boAmt;
    private BigDecimal cancelAmt;
    private BigDecimal shipmentAmt;
    private BigDecimal returnAmt;
    private BigDecimal poAmt;
    private BigDecimal poEoAmt;
    private BigDecimal poRoAmt;
    private BigDecimal poHoAmt;
    private BigDecimal poWoAmt;
    private BigDecimal receiveAmt;

    private BigDecimal allocateRate;
    private BigDecimal poEoRate;
    private BigDecimal poRoRate;
    private BigDecimal poHoRate;
    private BigDecimal poWoRate;
    private BigDecimal allocateAmtRate;
    private BigDecimal poEoAmtRate;
    private BigDecimal poRoAmtRate;
    private BigDecimal poHoAmtRate;
    private BigDecimal poWoAmtRate;

}