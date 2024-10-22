package com.a1stream.domain.bo.master;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM050102SalesControlBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String partsCd;
    private String partsNm;
    private Long partsId;
    private BigDecimal stdRetailPrice;
    private String update1;
    private BigDecimal averageCost;
    private String update2;
    private BigDecimal lastCost;
    private String update3;
    private BigDecimal salesLot;
    private BigDecimal qty;
    private String unavailable;

    private String brandCd;
    private BigDecimal purchaseLot;
    private BigDecimal purchaseQty;
    private BigDecimal stdPurchasePrice;

}