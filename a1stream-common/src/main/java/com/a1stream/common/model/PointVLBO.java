package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointVLBO extends BaseVLBO {

    private static final long serialVersionUID = 1L;

    private String cityNm;

    private Long cityId;

    private String provinceNm;

    private Long provinceId;

    private String warehouseFlag;
}
