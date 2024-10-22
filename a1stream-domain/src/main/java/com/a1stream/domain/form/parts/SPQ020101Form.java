package com.a1stream.domain.form.parts;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ020101Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private Long customerId;
    private String point;
    private String customer;
    private String orderNo;
    private String orderStatus;
    private String orderSourceType;
    private String dateFrom;
    private String dateTo;
}
