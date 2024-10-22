package com.a1stream.domain.bo.master;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM050101BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String brandCd;
    private String brandId;
    private String partsCd;
    private String partsNm;
    private Long partsId;
    private String largeGroupNm;
    private String middleGroupNm;
    private Long middleGroupId;
    private String registrationDate;
    private BigDecimal priceExcludeVAT;
    private BigDecimal priceIncludeVAT;
    private BigDecimal stdPurchasePrice;

    private String siteId;

}