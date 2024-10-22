package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseVLForm {

    private String content;

    private String arg0;

    private String arg1;

    private Integer pageSize;

    private Integer currentPage;
}
