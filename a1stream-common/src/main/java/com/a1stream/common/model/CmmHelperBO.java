package com.a1stream.common.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmmHelperBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    private String name;

    private Long id;

    private String desc;
}