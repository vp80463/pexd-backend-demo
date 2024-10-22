package com.a1stream.domain.vo;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SdPsiDwVO {

    private Long facilityId;

    private String siteId;

    private String facilityCd;

    private String facilityNm;

    private Long productId;

    private String productCd;

    private String productNm;

    private String colorCd;

    private String colorNm;

    private String psiDate;

    private BigDecimal beginingStock = BigDecimal.ZERO;

    private BigDecimal ymvnIn = BigDecimal.ZERO;

    private BigDecimal wholeSalesIn = BigDecimal.ZERO;

    private BigDecimal transferIn = BigDecimal.ZERO;

    private BigDecimal retailOut = BigDecimal.ZERO;

    private BigDecimal wholeSalesOut = BigDecimal.ZERO;

    private BigDecimal transferOut = BigDecimal.ZERO;

    private BigDecimal inTransit = BigDecimal.ZERO;

    private String createDatetime;
}
