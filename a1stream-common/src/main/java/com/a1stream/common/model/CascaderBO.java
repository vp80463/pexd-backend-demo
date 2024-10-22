package com.a1stream.common.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CascaderBO {

    private static final long serialVersionUID = 1L;

    private String label;

    private Long value;

    private String valueStr;

    private List<CascaderBO> children;
}