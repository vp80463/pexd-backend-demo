package com.a1stream.domain.bo.unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDQ010602BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String receiptPoint;

    private String receiptDate;

    private String receiptStatus;

    private String supplier;

    private String deliveryNoteNo;

    private String supplierDeliveryDate;

    private String fromPoint;

    private String transactionType;

    private String pic;

    private List<SDQ010602DetailBO> detailList = new ArrayList<>();

}
