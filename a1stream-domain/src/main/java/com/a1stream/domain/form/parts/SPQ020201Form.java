package com.a1stream.domain.form.parts;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ020201Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private Long partsId;
    private String point;
    private String parts;
    private String dateFrom;
    private String dateTo;
    private String allocationOnly ;
    private String inProcessOnly;
}
