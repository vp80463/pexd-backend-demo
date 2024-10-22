package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ030601BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Integer seqNo;

    private String wz;

    private String point;

    private Long wzId;

    private String location;

    private Long locationId;

    private String partsNo;

    private String partsNm;

    private Long partsId;

    private BigDecimal cost;

    private BigDecimal currentStock;

}
