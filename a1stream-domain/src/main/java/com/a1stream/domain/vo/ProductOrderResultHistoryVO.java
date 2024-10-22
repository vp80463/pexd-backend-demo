package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ProductOrderResultHistoryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long productOrderResultHistoryId;

    private String targetYear;

    private Long facilityId;

    private Long productId;

    private BigDecimal month01Qty = BigDecimal.ZERO;

    private BigDecimal month02Qty = BigDecimal.ZERO;

    private BigDecimal month03Qty = BigDecimal.ZERO;

    private BigDecimal month04Qty = BigDecimal.ZERO;

    private BigDecimal month05Qty = BigDecimal.ZERO;

    private BigDecimal month06Qty = BigDecimal.ZERO;

    private BigDecimal month07Qty = BigDecimal.ZERO;

    private BigDecimal month08Qty = BigDecimal.ZERO;

    private BigDecimal month09Qty = BigDecimal.ZERO;

    private BigDecimal month10Qty = BigDecimal.ZERO;

    private BigDecimal month11Qty = BigDecimal.ZERO;

    private BigDecimal month12Qty = BigDecimal.ZERO;

    private String backupDate;
}
