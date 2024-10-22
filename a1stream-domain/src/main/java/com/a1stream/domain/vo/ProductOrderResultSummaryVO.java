package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ProductOrderResultSummaryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long productOrderResultSummaryId;

    private String targetYear;

    private Long facilityId;

    private Long productId;

    private BigDecimal month01Quantity = BigDecimal.ZERO;

    private BigDecimal month02Quantity = BigDecimal.ZERO;

    private BigDecimal month03Quantity = BigDecimal.ZERO;

    private BigDecimal month04Quantity = BigDecimal.ZERO;

    private BigDecimal month05Quantity = BigDecimal.ZERO;

    private BigDecimal month06Quantity = BigDecimal.ZERO;

    private BigDecimal month07Quantity = BigDecimal.ZERO;

    private BigDecimal month08Quantity = BigDecimal.ZERO;

    private BigDecimal month09Quantity = BigDecimal.ZERO;

    private BigDecimal month10Quantity = BigDecimal.ZERO;

    private BigDecimal month11Quantity = BigDecimal.ZERO;

    private BigDecimal month12Quantity = BigDecimal.ZERO;

}
