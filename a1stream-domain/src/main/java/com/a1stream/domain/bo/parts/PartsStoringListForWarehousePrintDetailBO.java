package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsStoringListForWarehousePrintDetailBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String receiptNo;
    private String receiptDate;
    private String partsNo;
    private String partsName;
    private String pointAbbr;
    private String supplier;
    private String fromPoint;
    private BigDecimal receiptQty;
    private String lineNo;
    private String caseNo;
    private BigDecimal instrQty;
    private String locationType;
    private String mainSign;
    private BigDecimal locationStockQty;
    private BigDecimal boQty;
    private String locationCd;
    private String orderNo;
    private BigDecimal actualQty;
    private BigDecimal actualLoc;
    
}
