package com.a1stream.domain.bo.unit;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM010601BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String receiptSlipStatus;

    private String deliveryNoteNo;

    private String deliveryDate;

    private String transactionType;

    private String transactionTypeCd;

    private Long supplier;

    private Long fromPointId;

    private String receiptDate;

    private String supplierCd;

    private String supplierNm;

    private String fromPointCd;

    private BigDecimal receiptQty;

    private Long receiptSlipId;

    private String receiptSlipNo;

    //控制checkBox
    private String checkFlg;

    private Long promotionListId;

    private Long promotionModelId;

    private String frameNo;

    private Long promotionItemId;

}
