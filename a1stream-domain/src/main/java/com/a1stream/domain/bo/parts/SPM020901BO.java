package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM020901BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long partsId;

    private String partsCd;

    private String partsNm;

    private BigDecimal onHandQty = BigDecimal.ZERO;

    private BigDecimal instructionQty = BigDecimal.ZERO;

    private BigDecimal actualQty = BigDecimal.ZERO;

    private Long locationId;

    private Integer piUpdateCount;
}
