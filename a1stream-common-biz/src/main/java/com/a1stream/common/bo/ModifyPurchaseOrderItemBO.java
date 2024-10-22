package com.a1stream.common.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyPurchaseOrderItemBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long purchaseOrderItemId;

    private Long partsId;

    private String partsNo;

    private String partsName;

    private String boCancelFlag;

    private String deleteFlag;

    private BigDecimal purchaseQty;

    private BigDecimal purchasePrice;

    private BigDecimal stdPrice;
}