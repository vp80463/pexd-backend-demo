package com.a1stream.domain.form.parts;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM030603Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String fromPoint;

    private Long fromPointId;

    private String toPoint;

    private Long toPointId;

    private String duNo;

    private Long deliveryOrderId;

}
