package com.a1stream.common.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsInfoBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long partsId;

    private String partsNo;

    private String partsNm;

    private String brand;

    private BigDecimal salLotSize;

    private String batteryFlag;

    private String largeGroup;

    private String middleGroup;

    private BigDecimal minPurQty;

    private BigDecimal stdRetailPrice;

    private String supersedingPartsId;

    private String supersedingPartsCd;

    private String supersedingPartsNm;

    private Long mainLocationId;

    private BigDecimal onHandQty;

    private BigDecimal taxRate;

    private BigDecimal price;

    private String salesStatusType;

    private String partStatus;

    private Long id;

    private String mainLocationCd;

    private BigDecimal purLotSize;

    private Long productCategoryId;

    private String codeFmt;

    private String desc;
}