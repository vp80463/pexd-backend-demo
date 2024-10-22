package com.a1stream.domain.form.master;

import com.a1stream.common.model.BaseForm;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * @author dong zhen
 */
@Setter
@Getter
public class HomePageForm extends BaseForm {

    @Serial
    private static final long serialVersionUID = 6467876919444547707L;

    /**
     * 条件1
     */
    private String condition1;

    /**
     * 类别
     */
    private String businessType;
}
