package com.a1stream.domain.form.unit;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.unit.SDM010601BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM010601Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long receiptPointId;

    private String deliveryDateFrom;

    private String deliveryDateTo;

    private String receiptStatus;

    private String deliveryNoteNo;

    private String transactionType;

    private Long supplierId;

    private String supplier;

    private Long fromPointId;

    private String receiptDateFrom;

    private String receiptDateTo;

    private List<SDM010601BO> gridData;

}
