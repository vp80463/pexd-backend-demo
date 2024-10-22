package com.a1stream.domain.bo.unit;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010402TransactionHistoryBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String pointCd;

    private String pointNm;

    private String transactionTypeId;

    private String transactionDate;

    private String transactionNo;

    private String pic;

    private Long toConsumerId;

    private Long fromPartyId;

    private Long toPartyId;

    private Long fromFacilityId;

    private Long toFacilityId;

    private String from;

    private String to;

}
