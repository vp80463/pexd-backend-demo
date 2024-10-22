package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ030801BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String pointCd;

    private String pointNm;

    private String pointvl;

    private String locationType;

    private String locationTypeId;

    private Long binTypeId;

    private String binType;

    private BigDecimal inUseQty = BigDecimal.ZERO;

    private BigDecimal emptyQty = BigDecimal.ZERO;

    private BigDecimal totalQty = BigDecimal.ZERO;

    private BigDecimal usageRate = BigDecimal.ZERO;
}
