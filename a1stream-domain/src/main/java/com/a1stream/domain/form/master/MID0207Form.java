package com.a1stream.domain.form.master;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.master.ProductPriceBO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.List;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class MID0207Form extends BaseForm {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<ProductPriceBO> importList;

    private Object otherProperty;
}
