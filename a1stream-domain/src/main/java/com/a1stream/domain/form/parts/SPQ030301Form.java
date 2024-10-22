package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ030301Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private Long partsId;

    private String parts;

    private List<String> productCategory;

    private Boolean pageFlg = Boolean.TRUE;
}
