package com.a1stream.common.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsVLBO extends BaseVLBO {

    private static final long serialVersionUID = 1L;

    private String brand;

    private BigDecimal salLotSize;

    private String batteryFlag;

    private String largeGroup;

    private String middleGroup;

    private BigDecimal minPurQty;

    private BigDecimal stdRetailPrice;

    private String supersedingPartsId;

    private String supersedingPartsCd;

    private String supersedingPartsCdFmt;

    private String supersedingPartsNm;

    private String mainLocationId;

    private String mainLocationCd;

    private BigDecimal onHandQty;

    private BigDecimal taxRate;

    private BigDecimal price;

    private String codeFmt;

    private String partStatus;

    private BigDecimal purLotSize;

    private Long productCategoryId;
}
