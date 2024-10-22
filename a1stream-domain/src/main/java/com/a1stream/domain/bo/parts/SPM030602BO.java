package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM030602BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String date;

    private String duNo;

    private BigDecimal transferQty;

    private String fromPoint;

    private Long fromPointId;

    private String toPoint;

    private Long toPointId;

    private Long deliveryOrderId;

    private String receipt;

}
