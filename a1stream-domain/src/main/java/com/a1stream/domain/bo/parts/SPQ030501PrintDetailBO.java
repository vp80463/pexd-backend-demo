package com.a1stream.domain.bo.parts;

import java.io.Serial;
import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

/**
*
* 功能描述:Parts On-Working Check List Inquiry Print
*
* mid2330
* 2024年6月17日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/17   Liu Chaoran      New
*/
@Getter
@Setter
public class SPQ030501PrintDetailBO extends BaseBO {

    @Serial
    private static final long serialVersionUID = 1L;

    private String partsNo;

    private String partsNm;

    private BigDecimal onReceivingQty;

    private BigDecimal onPickingQty;

    private BigDecimal onServiceQty;

    private BigDecimal onFrozenQty;

    private BigDecimal onTransferInQty;
}