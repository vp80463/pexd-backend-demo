package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsSalesReturnInvoiceForFinancePrintDetailBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String partsNo;

    private String partsNm;

    private BigDecimal returnQty;

    private BigDecimal sellingPrice;

    private BigDecimal returnPrice;

    private BigDecimal returnAmount;
}
