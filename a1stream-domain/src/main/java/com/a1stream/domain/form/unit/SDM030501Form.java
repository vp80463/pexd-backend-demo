package com.a1stream.domain.form.unit;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.unit.SDM030501BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM030501Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String orderMonth;

    private String orderStatus;

    private String employeeCd;

    private List<SDM030501BO> importList;

    private String userType;
}
