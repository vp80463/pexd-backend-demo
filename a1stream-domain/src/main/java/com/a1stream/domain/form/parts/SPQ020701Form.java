package com.a1stream.domain.form.parts;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ020701Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String dateFrom;

    private String dateTo;

    private String orderDateFrom;

    private String orderDateTo;

    private String orderNo;

    private Long partsId;

    private String point;

    private String parts;
}
