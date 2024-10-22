package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM031001BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String type;

    private BigDecimal lines;

    private BigDecimal items;

    private BigDecimal qty;

    private BigDecimal amount;

    private String seq;

    private Long productId;

    private String stockStatusType;

    private BigDecimal actualQty;

    private BigDecimal expectedQty;

    private Long locationId;

    private Long productInventoryId;

    private String productCd;

    private String productNm;

    private String locationCd;

    private BigDecimal inCost;

    private BigDecimal currentAverageCost;

    //打印携带百分比用
    private String linesPrecent;

    private String itemsPrecent;

    private String qtyPrecent;

    private String amountPrecent;

    private String middleGroupCd;

    private String middleGroupNm;

    private String largeGroupCd;

    private String largeGroupNm;

    private BigDecimal cost;

    private BigDecimal gainQty;

    private BigDecimal gainActQty;

    private BigDecimal averageCost;
}
