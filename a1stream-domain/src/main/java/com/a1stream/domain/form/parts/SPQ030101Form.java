package com.a1stream.domain.form.parts;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ030101Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String point;

    private Long supplierId;

    private String supplier;

    private String dateFrom;

    private String dateTo;

    private Long partsId;

    private String parts;

    private String receiptNo;

    private String purchaseOrderNo;

    private String transactionType;

    private Long storingLineId;

    //为N代表非spq030101页面跳转过来的
    private String flag;

}
