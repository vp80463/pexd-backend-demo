package com.a1stream.domain.bo.parts;

import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM020201PrintBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String returnInvoiceNo;

    private String invoiceNo;

    private String point;

    private String customer;

    private String returnDate;

    private List<SPM020201PrintDetailBO> detailList;

    private String date;

    private String logo;
}
