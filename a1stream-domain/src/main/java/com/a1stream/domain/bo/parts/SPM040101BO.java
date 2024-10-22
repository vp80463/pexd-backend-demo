package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM040101BO extends BaseBO{

    private static final long serialVersionUID = 1L;
    private Long abcDefinitionID;
    private String costUsage;
    private String targetSupply;
    private BigDecimal safetyFactor;
    private BigDecimal ssmUpper;
    private BigDecimal ssmLower;
    private Integer addLTDays;
    private BigDecimal ropMonth;
    private BigDecimal roqMonth;
}