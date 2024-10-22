package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class SeasonIndexBatchVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long seasonIndexBatchId;

    private Long facilityId;

    private Long productCategoryId;

    private String manualFlag;

    private BigDecimal nIndex = BigDecimal.ZERO;

    private BigDecimal n1Index = BigDecimal.ZERO;

    private BigDecimal n2Index = BigDecimal.ZERO;

    private BigDecimal n3Index = BigDecimal.ZERO;

    private BigDecimal n4Index = BigDecimal.ZERO;

    private BigDecimal n5Index = BigDecimal.ZERO;

    private BigDecimal n6Index = BigDecimal.ZERO;

    private BigDecimal n7Index = BigDecimal.ZERO;

    private BigDecimal n8Index = BigDecimal.ZERO;

    private BigDecimal n9Index = BigDecimal.ZERO;

    private BigDecimal n10Index = BigDecimal.ZERO;

    private BigDecimal n11Index = BigDecimal.ZERO;
}
