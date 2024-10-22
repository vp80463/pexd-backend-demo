package com.a1stream.domain.bo.common;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuBO extends MenuMetaBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String path;

    private String name;

    private String id;

    private String parentId;
}