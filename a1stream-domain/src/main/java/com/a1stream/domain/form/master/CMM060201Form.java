package com.a1stream.domain.form.master;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM060201Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String siteId;
    private String packageNo;
    private String packageCd;
    private String packageNm;
    private Long packageId;
    private String validDate;
    private Long productCategoryId;
}