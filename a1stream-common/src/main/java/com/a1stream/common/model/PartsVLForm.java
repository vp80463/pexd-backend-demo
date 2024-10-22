package com.a1stream.common.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsVLForm extends BaseVLForm {

    private String code;

    private String batteryFlag;

    private String brandId;

    private List<String> prodCtg;

    private String autoFillFlag;

    private Long facilityId;

    private String largeMiddleGroup;

    private String partsCd;

    //给ypec查询使用
    private List<String> partsCds;

    private List<Long> partIds;

    private String siteId;
}
