package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM020103PrintDetailBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String parts;
    private String partsNo;
    private String partsName;
    private Long batteryId;
    private BigDecimal discountAmount;
    private String orderDate;
    private String cancelReason;
    private BigDecimal productTax;
    private BigDecimal originalQty;
    private BigDecimal sellingPrice;
    private BigDecimal specialPrice;
    private BigDecimal standardPrice;
    private String discountOffRate;
    private BigDecimal allocatedQty;
    private BigDecimal boQty;
    private BigDecimal cancelQty;
    private BigDecimal amount;
    private BigDecimal sl;
    private BigDecimal currencyVat;
    private BigDecimal totalAmount;
    private String taxRate;
}
