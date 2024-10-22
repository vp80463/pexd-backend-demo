package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class SeasonIndexManualVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long seasonIndexManualId;

    private Long facilityId;

    private Long productCategoryId;

    private String manualFlag;

    private BigDecimal indexMonth01 = BigDecimal.ZERO;

    private BigDecimal indexMonth02 = BigDecimal.ZERO;

    private BigDecimal indexMonth03 = BigDecimal.ZERO;

    private BigDecimal indexMonth04 = BigDecimal.ZERO;

    private BigDecimal indexMonth05 = BigDecimal.ZERO;

    private BigDecimal indexMonth06 = BigDecimal.ZERO;

    private BigDecimal indexMonth07 = BigDecimal.ZERO;

    private BigDecimal indexMonth08 = BigDecimal.ZERO;

    private BigDecimal indexMonth09 = BigDecimal.ZERO;

    private BigDecimal indexMonth10 = BigDecimal.ZERO;

    private BigDecimal indexMonth11 = BigDecimal.ZERO;

    private BigDecimal indexMonth12 = BigDecimal.ZERO;
}
