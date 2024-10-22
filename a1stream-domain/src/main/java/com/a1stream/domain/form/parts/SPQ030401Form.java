package com.a1stream.domain.form.parts;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ030401Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String point;
    private Long pointId;
    private String parts;
    private Long partsId;
    private String dateFrom;
    private String dateTo;
}
