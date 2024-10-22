package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ030701BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Integer seqNo;

    private String wz;

    private Long wzId;

    private String location;

    private Long locationId;

    private String partsNo;

    private String partsNm;

    private Long partsId;

    private BigDecimal systemQty;

    private BigDecimal actualQty;

    private String inputSign;

    private String newFoundFlag;

    private String point;
}
