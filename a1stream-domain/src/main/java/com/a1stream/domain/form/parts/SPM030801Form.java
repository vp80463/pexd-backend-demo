package com.a1stream.domain.form.parts;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM030801Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private Long facilityId;

    private String stockTakingRange;

    private String pointCd;

    private String pointNm;
}
