package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM060201BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String packageNo;
    private String packageName;
    private String productCategory;
    private String validDateFrom;
    private String validDateTo;
    private Long servicePackageId;
    private String categoryNm;
}