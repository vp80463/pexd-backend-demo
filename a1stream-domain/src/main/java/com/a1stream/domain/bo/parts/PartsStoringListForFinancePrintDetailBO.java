package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsStoringListForFinancePrintDetailBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String lineNo;
    private String partsNo;
    private String partsName;
    private String caseNo;
    private BigDecimal receiptQty;
    private BigDecimal receiptCost;
}
