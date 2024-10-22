package com.a1stream.domain.form.parts;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ020601Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String point;

    private Long pointId;

    private String duNo;

    private String dateFrom;

    private String dateTo;

    private String transactionType;

    private String duStatus;

    private String unfinishedOnly;

    private Long deliveryOrderId;

    private Long pickingListId;

    private Boolean pageFlg = Boolean.TRUE;

}
