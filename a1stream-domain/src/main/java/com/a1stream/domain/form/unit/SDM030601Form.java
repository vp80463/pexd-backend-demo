package com.a1stream.domain.form.unit;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.unit.SDM030601BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM030601Form extends BaseForm {

    private static final long serialVersionUID = 1L;
    
    private SDM030601BO formData;
}
