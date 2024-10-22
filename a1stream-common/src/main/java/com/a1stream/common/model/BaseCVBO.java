package com.a1stream.common.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseCVBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    private String name;

    private String id;

    public BaseCVBO() {}

    public BaseCVBO(String code, String name, String id) {

        this.code = code;
        this.name = name;
        this.id   = id;
    }
}