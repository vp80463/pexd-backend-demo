package com.a1stream.domain.form.parts;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ020302Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long invoiceId;
    
    private boolean pageFlg = true;

}
