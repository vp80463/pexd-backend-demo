package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;
import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DIM020601BO extends BaseBO{

    private static final long serialVersionUID = 1L;

    private String dealerCd;

    private Long pointId;

    private String pointCd;

    private Long locationId;

    private String locationCd;

    private Long productId;

    private String productNm;

    private String partsNo;

    private BigDecimal qty;

    private BigDecimal averageCost;

    private Integer seq;

    private String type;

    private String errorMessage;

    private String warningMessage;

    transient List<String> error;

    transient List<Object[]> errorParam;

    transient List<String> warning;

    transient List<Object[]> warningParam;
}
