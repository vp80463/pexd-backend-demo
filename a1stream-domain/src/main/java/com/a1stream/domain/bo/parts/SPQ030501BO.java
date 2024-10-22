package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ030501BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String partsNo;

    private String partsNm;

    private String partsId;

    private BigDecimal onReceivingQty;

    private BigDecimal onPickingQty;

    private BigDecimal onFrozenQty;

    private BigDecimal onTransferInQty;

}
