package com.a1stream.domain.form.parts;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ030701Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String point;

    private Long partsId;

    private String parts;

    private Long wzId;

    private String inputSign;

    private String showDifferentOnly;

    private Boolean pageFlg = Boolean.TRUE;

}
