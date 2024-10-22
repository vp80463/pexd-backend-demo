package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ020602BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String pointCd;

    private String pointNm;

    private String pickingListNo;

    private String pickingNo;

    private String duNo;

    private String customerCd;

    private String customerNm;

    private String instructionDate;

    private String transactionTypeCd;

    private String transactionType;

    private String duStatus;

    private String duStatusCd;

    private Long pickingListId;

    private Long deliveryOrderId;

    private String seqNo;

    private String locationCd;

    private String productCd;

    private String productNm;

    private BigDecimal pickingQty;
}
