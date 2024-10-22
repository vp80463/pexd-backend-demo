package com.a1stream.domain.form.master;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM021101Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long binTypeId;

    private String binTypeCd;

    private String description;

    private Integer length;

    private Integer width;

    private Integer height;

    private BigDecimal volume;
}
