package com.a1stream.domain.bo.parts;

import java.io.Serial;
import java.util.List;

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
public class SPQ030101PrintBO extends BaseBO {

    @Serial
    private static final long serialVersionUID = 1L;

    private String pointAbbr;

    private String deliveryNo;

    private String receiptNo;

    private String receiptDate;

    private String date;

    private String logo;

    private String qrCode;

    private String barCode;

    private List<SPQ030101BO> detailPrintList;
}