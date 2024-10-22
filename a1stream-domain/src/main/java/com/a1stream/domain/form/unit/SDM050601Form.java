package com.a1stream.domain.form.unit;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.unit.SDM050601BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050601Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String promotion;

    private Long promotionId;

    private String dateFrom;

    private String dateTo;

    private Long defaultPointId;

    private Boolean userFlag = Boolean.FALSE;

    private List<SDM050601BO> gridData;
}
