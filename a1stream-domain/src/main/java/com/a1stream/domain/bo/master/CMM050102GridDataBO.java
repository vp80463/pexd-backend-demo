package com.a1stream.domain.bo.master;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM050102GridDataBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private BigDecimal averageCost;
    private String brandCd;
    private String costUsage;
    private String fromSiteId;
    private BigDecimal height;
    private BigDecimal length;
    private BigDecimal lastCost;
    private String localNm;
    private String mainLocation;
    private String middleGroupCd;
    private String partsCd;
    private Long partsId;
    private String partsNm;
    private Long pointId;
    private String pointCd;
    private String pointNm;
    private BigDecimal purchaseLot;
    private BigDecimal purchaseQty;
    private BigDecimal qty;
    private String registrationDate;
    private BigDecimal rop;
    private String ropAndRoqSign;
    private BigDecimal roq;
    private BigDecimal salesLot;
    private String salesNm;
    private String siteId;
    private BigDecimal stdPriceaverageCost;
    private BigDecimal stdPurchasePrice;
    private String toSiteId;
    private String unavailable;
    private String update1;
    private String update2;
    private String update3;
    private Integer updateCount;
    private String updateProgram;
    private BigDecimal volumn;
    private BigDecimal weight;
    private BigDecimal width;

}