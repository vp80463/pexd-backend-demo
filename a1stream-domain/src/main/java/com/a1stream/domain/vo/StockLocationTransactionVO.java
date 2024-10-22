package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class StockLocationTransactionVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long locationTransactionId;

    private Integer seqNo = CommonConstants.INTEGER_ZERO;

    private String locationTransactionType;

    private String transactionNo;

    private Long productId;

    private String productCd;

    private String productNm;

    private Long personId;

    private Long facilityId;

    private Long locationId;

    private BigDecimal moveQty = BigDecimal.ZERO;

    private String transactionDate;

    private String transactionTime;

    private Long relationSlipId;

    private Long relationSlipItemId;

    private String reason;
}
