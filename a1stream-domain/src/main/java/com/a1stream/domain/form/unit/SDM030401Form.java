package com.a1stream.domain.form.unit;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.unit.SDM030401BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM030401Form extends BaseForm {

    private static final long serialVersionUID = 1L;
    
    private SDM030401BO formData;
}
