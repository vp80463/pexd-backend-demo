package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM040402BO extends BaseBO{

    private static final long serialVersionUID = 1L;

    private String partsNo;

    private String partsNm;

    private Long partsId;

    private BigDecimal price;

    private BigDecimal minPurchaseQty;

    private BigDecimal purchaseLot;

    private BigDecimal orderQty;

    private BigDecimal orderAmount;

    private String cancelFlag;

    private Long purchaseOrderItemId;

    private Long purchaseOrderId;

    private BigDecimal stdRetailPrice;

    private Integer seqNo;

}
