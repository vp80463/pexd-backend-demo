package com.a1stream.domain.bo.master;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMQ050101StockInfoBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private String pointCd;
    private String pointNm;
    private BigDecimal stockQty;
    private BigDecimal backOrder;
    private BigDecimal allocatedQty;
    private BigDecimal onPurchaseQty;
    private BigDecimal onHandQty;
    private BigDecimal onReceivingQty;
    private BigDecimal onFrozenQty;
    private BigDecimal onPickingQty;
    private BigDecimal onTransferQty;

}