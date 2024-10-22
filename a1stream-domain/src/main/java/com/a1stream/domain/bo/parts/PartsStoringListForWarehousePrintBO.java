package com.a1stream.domain.bo.parts;

import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsStoringListForWarehousePrintBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String pointAbbr;

    private String deliveryNo;

    private String receiptNo;

    private String receiptDate;

    private String date;

    private String logo;

    private String qrCode;

    private String barCode;

    private List<PartsStoringListForWarehousePrintDetailBO> detailPrintList;

}
