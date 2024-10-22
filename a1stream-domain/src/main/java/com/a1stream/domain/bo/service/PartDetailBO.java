package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartDetailBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serviceCategoryId;
    private Long serviceDemandId;
    private String serviceDemandContent;
    private String settleTypeId;
    private Long symptomId;
    private Long partId;
    private String partCd;
    private String partNm;
    private Long supersedingPartId;
    private String supersedingPartCd;
    private String supersedingPartsCdFmt;
    private String supersedingPartNm;
    private BigDecimal standardPrice;
    private BigDecimal discountAmt;
    private BigDecimal discount;
    private BigDecimal specialPrice;
    private BigDecimal sellingPrice;
    private BigDecimal qty;
    private BigDecimal amount;
    private BigDecimal onHandQty;
    private BigDecimal boQty;
    private BigDecimal allocatedQty;
    private BigDecimal shippedQty;
    private BigDecimal ymvnStockQty;
    private String locationCd;
    private String batteryFlag;
    private String batteryType;
    private Long servicePackageId;
    private String servicePackageCd;
    private String servicePackageNm;
    private BigDecimal taxRate;
    private Integer seqNo;
    private String updateCounter;
    private BigDecimal sellingPriceNotVat;
    private BigDecimal actualAmtNotVat;

    private Long salesOrderItemId;
}
