package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsPickingListByOrderPrintBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String wz;
    private String locationCd;
    private String partsNo;
    private String partsName;
    private BigDecimal pickQty;
    private String pickSeqNo;
    private String duNo;
    private Long duId;
    private String point;
    private String orderNo;
    private String date;
}
