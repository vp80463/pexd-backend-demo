package com.a1stream.domain.form.unit;

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
public class SDQ070401Form extends BaseForm{

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private String fromMonth;
    private String toMonth;
    private boolean pageFlg = true;
}
