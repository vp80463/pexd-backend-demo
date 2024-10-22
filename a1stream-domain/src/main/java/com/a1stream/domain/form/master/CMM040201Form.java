package com.a1stream.domain.form.master;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM040201Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String sectionCd;
    private String sectionNm;
    private Long sectionId;
    private String sectionCode;

    private String faultSectionCd;
    private String faultSectionNm;
    private Long faultSectionId;
    private String faultSectionCode;
}