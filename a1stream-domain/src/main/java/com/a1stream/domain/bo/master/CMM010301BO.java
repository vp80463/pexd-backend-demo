package com.a1stream.domain.bo.master;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM010301BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String consumerType;
    private String consumerNm;
    private String vipNo;
    private String phone;
    private String idNo;
    private BigDecimal qty;
    private Long consumerId;
}