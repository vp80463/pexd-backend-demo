package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmServiceHistoryItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceHistoryItemId;

    private Long productId;

    private String productContent;

    private String productClassification;

    private BigDecimal qty = BigDecimal.ZERO;

    private BigDecimal standardPrice = BigDecimal.ZERO;

    private BigDecimal sellingPrice = BigDecimal.ZERO;

    private BigDecimal amount = BigDecimal.ZERO;

    private String serviceCategory;

    private Long serviceDemand;

    private Long serviceHistoryId;


}
