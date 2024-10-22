package com.a1stream.domain.bo.master;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM050102PurchaseControlBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String pointCd;
    private Long pointId;
    private String pointNm;
    private Long locationId;
    private String mainLocation;
    private String ropAndRoqSign;
    private BigDecimal rop;
    private BigDecimal roq;
    private String costUsage;

}