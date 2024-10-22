package com.a1stream.domain.bo.unit;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDQ011401BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long facilityId;

    private String pointCd;

    private String pointNm;

    private Long modelId;

    private String modelCd;

    private String modelNm;

    private String transactionNo;

    private Long relatedSlipId;

    private String transactionTypeCd;
    
    private String transactionTypeNm;

    private String transactionDate;

    private Long fromFacilityId;

    private Long toFacilityId;

    private Long fromOrganizationId;

    private Long toOrganizationId;

    private BigDecimal inQty;

    private BigDecimal outQty;

    private BigDecimal stockQty;

    private String from;

    private String to;

}
