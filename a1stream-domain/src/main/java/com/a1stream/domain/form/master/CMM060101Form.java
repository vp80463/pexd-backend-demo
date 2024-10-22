package com.a1stream.domain.form.master;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM060101Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String serviceJob;
    private String serviceJobName;
}