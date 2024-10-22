package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ030301BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String partsNo;
    private String partsNm;
    private String partsId;
    private String supersedingParts;
    private String supersedingPartsId;
    private BigDecimal onHandQty;
    private BigDecimal allocatedQty;
    private BigDecimal boQty;
    private BigDecimal onReceivingQty;
    private BigDecimal onPickingQty;
    private BigDecimal hoQty;
    private BigDecimal eoQty;
    private BigDecimal roQty;
    private BigDecimal woQty;
    private BigDecimal onTransferQty;
    private BigDecimal onServiceQty;
    private BigDecimal onFrozenQty;
    private BigDecimal averageCost;
    private String largeGroup;
    private String middleGroup;

}
