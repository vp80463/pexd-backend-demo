package com.a1stream.domain.bo.batch;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsDeadStockItemBO {

    private String dealerCode;
    private String consigneeCode;
    private String partNo;
    private String stockAdjustExistSign;
    private String deadStockQty;
    private Long facilityId;
    private String facilityCd;
    private Long productId;
    private String productCd;
    private String productStockStatusTypeDbId;
    private BigDecimal quantity;
    private String abcType;
    private String stringValue;
    private String ropqType;
}
