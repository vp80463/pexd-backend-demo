package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicePackageVLBO extends BaseVLBO {

    private static final long serialVersionUID = 1L;

    private String packageNo;
    private String packageName;
    private String productCategory;
    private String validDateFrom;
    private String validDateTo;
    private Long servicePackageId;
}
