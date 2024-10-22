package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Getter
@Setter
public class SPQ050201BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String customerCd;
    private String customerNm;
    private Integer soLine;
    private Integer allocatedLine;
    private BigDecimal allocatedAmt;
    private Integer boLine;
    private BigDecimal boAmt;
    private Integer soCancelLine;
    private Integer shipmentLine;
    private Integer returnLine;
    private BigDecimal invoiceAmt;
    private BigDecimal invoiceCost;
    private BigDecimal allocatedRate;
    private BigDecimal salesProfit;


}