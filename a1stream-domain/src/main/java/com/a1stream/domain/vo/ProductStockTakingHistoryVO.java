package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ProductStockTakingHistoryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long productStockTakingHistoryId;

    private Long facilityId;

    private String startedDate;

    private String startedTime;

    private String finishedDate;

    private String finishedTime;

    private BigDecimal logicAmt = BigDecimal.ZERO;

    private BigDecimal physicalAmt = BigDecimal.ZERO;

    private BigDecimal gapAmt = BigDecimal.ZERO;

    private Long equalLines;

    private Long equalItems;

    private BigDecimal equalQty = BigDecimal.ZERO;

    private BigDecimal equalAmt = BigDecimal.ZERO;

    private Long exceedLines;

    private Long exceedItems;

    private BigDecimal exceedQty = BigDecimal.ZERO;

    private BigDecimal exceedAmt = BigDecimal.ZERO;

    private Long lackLines;

    private Long lackItems;

    private BigDecimal lackQty = BigDecimal.ZERO;

    private BigDecimal lackAmt = BigDecimal.ZERO;

    private String productClassification;


}
