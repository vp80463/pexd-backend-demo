package com.a1stream.domain.form.parts;

/**
*
* 功能描述:
*
* @author mid2215
*/
import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SPQ050801Form extends BaseForm{

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private String point;
    private String pointCd;
    private String targetMonth;
    private String largeGroupCd;
}
