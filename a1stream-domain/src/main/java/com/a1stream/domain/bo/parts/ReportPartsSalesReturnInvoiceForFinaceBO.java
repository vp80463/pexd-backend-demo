package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportPartsSalesReturnInvoiceForFinaceBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String partsNo;
    private String partsNm;
    private BigDecimal returnQty = BigDecimal.ZERO;
    private BigDecimal returnPrice = BigDecimal.ZERO;
    private BigDecimal returnAmount = BigDecimal.ZERO;
    private BigDecimal cost = BigDecimal.ZERO;
    private BigDecimal taxRate = BigDecimal.ZERO;
}
