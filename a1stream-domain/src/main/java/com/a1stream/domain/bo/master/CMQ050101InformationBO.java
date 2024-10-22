package com.a1stream.domain.bo.master;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMQ050101InformationBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String siteId;
    private String partsNm;
    private String salesNm;
    private String localNm;
    private String registrationDate;
    private String middleGroup;
    private String middleGroupCd;
    private String middleGroupNm;
    private String largeGroupId;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal weight;
    private String supersedingParts;
    private String supersedingPartsNm;
    private Long supersedingPartsId;
    private BigDecimal salesLot;
    private BigDecimal minSalesQty;
    private String unavailable;
    private Long brandId;
    private String brandCd;
    private String brandNm;
    private BigDecimal purchaseLot;
    private BigDecimal minPurchaseQty;
}