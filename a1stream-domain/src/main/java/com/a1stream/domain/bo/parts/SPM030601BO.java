package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM030601BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String partsNo;

    private String partsNm;

    private Long partsId;

    private BigDecimal onHandQty;

    private BigDecimal transferQty;

    private BigDecimal stdPrice;

    private BigDecimal taxRate;
}
