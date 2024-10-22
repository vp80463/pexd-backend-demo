package com.a1stream.domain.form.parts;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ030201Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private String point;
    private Long partsId;
    private String parts;
    private String stockAdjustmentReason;
    private String dateFrom;
    private String dateTo;
}
