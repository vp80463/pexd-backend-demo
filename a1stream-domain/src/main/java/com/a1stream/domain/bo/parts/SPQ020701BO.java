package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ020701BO extends BaseBO{

    private static final long serialVersionUID = 1L;

    private String orderNo;
    private String orderDate;
    private String partsCd;
    private String partsNm;
    private String partsAbbr;
    private Long partsId;
    private BigDecimal orderQty;
    private BigDecimal cancelQty;
    private String cancelDate;
    private String cancelTime;
    private String cancelPic;
}