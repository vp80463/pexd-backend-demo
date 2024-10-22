package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM030603BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String partsNo;

    private String partsNm;

    private BigDecimal transferQty;

    private String fromPoint;

    private Long fromPointId;

    private String toPoint;

    private Long toPointId;

    private String duNo;

}
