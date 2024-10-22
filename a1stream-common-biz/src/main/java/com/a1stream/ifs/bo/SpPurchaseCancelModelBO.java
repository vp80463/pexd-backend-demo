package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpPurchaseCancelModelBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dealerCode;
    private String partNo;
    private String yourOrderNo;
    private String cancelQty;

    private String siteId;
    private Long consigneeId;
    private String eoRoType;
    private Long poId;
    private Long poItemId;
    private Long productId;
    private BigDecimal minusQuantity;
    private String poNo;
    private String partsNo;
    private String partsNm;

    private Long pointId;
    private String pointNm;
    private String pointNo;
    private Long supplierId;
    private String supplierCd;
    private String supplierNm;
}