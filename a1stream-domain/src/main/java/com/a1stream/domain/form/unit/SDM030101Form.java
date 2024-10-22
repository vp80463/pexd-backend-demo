package com.a1stream.domain.form.unit;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM030101Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private String salesMonth;
    private String orderNo;
    private String frameNo;
    private String orderStatus;
    private String salesType;
    private Long dealerId;
    private String dealer;
    private Long consumerId;
    private String consumer;
}
