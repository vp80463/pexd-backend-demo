package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvoiceSerializedItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long invoiceSerializedItemId;

    private Long invoiceId;

    private Long invoiceItemId;

    private Long serializedProductId;
}
