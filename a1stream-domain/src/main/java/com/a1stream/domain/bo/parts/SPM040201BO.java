package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM040201BO extends BaseBO{

    private static final long serialVersionUID = 1L;
    private String partsCd;
    private Long reorderGuidelineId;
    private String partsNm;
    private Long partsId;
    private BigDecimal rop;
    private BigDecimal roq;
    private String sign;
    private String beforeSign;
    private String largeGroup;
    private String costUsage;
    private String totalOne;
    private String totalTwo;
    private BigDecimal averageCost;
    private BigDecimal stockQty;
    private BigDecimal onPurchaseQty;
    private BigDecimal boQty;
}