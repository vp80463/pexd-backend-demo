package com.a1stream.domain.form.unit;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDQ011401Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Boolean pageFlg = Boolean.TRUE;

    private String dateFrom;

    private String dateTo;

    private Long pointId;

    private String model;

    private Long modelId;

}
