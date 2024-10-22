package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmSerializedProductTranVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serializedProductTransactionId;

    private Long serializedProductId;

    private String transactionDate;

    private String transactionTime;

    private String relatedSlipNo;

    private String reporterNm;

    private String comment;

    private Long locationInfoId;

    private String transactionTypeId;

    private String fromStatus;

    private String toStatus;

    private Long productId;

    private Long fromPartyId;

    private Long toPartyId;

    private Long toConsumerId;

    private Long targetFacilityId;

    private Long fromFacilityId;

    private Long toFacilityId;

    private BigDecimal inCost = BigDecimal.ZERO;

    private BigDecimal outCost = BigDecimal.ZERO;

    private BigDecimal outPrice = BigDecimal.ZERO;


}
