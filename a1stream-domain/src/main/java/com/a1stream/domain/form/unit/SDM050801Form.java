package com.a1stream.domain.form.unit;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.unit.SDM050801BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050801Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String promotion;

    private Long promotionId;

    private String promotionCd;

    private String frameNo;

    private List<SDM050801BO> gridData;

}
