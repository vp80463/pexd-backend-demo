package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ030201BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String partsNo;
    private String partsNm;
    private Long partsId;
    private String adjustmentReason;
    private String adjustmentReasonTypeId;
    private String adjustmentDate;
    private String adjustmentTime;
    private String location;
    private Long locationId;
    private BigDecimal inQty;
    private BigDecimal inCost;
    private BigDecimal inAmount;
    private BigDecimal outQty;
    private BigDecimal outCost;
    private BigDecimal outAmount;
    private String pic;
}
