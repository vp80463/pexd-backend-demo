package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM020801BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long traderId;
    private String trader;
    private String traderNm;
    private String abbr;
    private String dealerSign;
    private String supplierSign;
    private String effectiveDate;
    private String expiredDate;

}