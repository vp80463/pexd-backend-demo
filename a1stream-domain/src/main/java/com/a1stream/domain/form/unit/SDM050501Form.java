package com.a1stream.domain.form.unit;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.unit.SDM050501BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050501Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private SDM050501BO formData;

    private Long promotionOrderId;

    private String uploadType;

    private String uploadPath;
}
