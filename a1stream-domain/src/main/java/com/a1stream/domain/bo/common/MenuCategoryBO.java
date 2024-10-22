package com.a1stream.domain.bo.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuCategoryBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String path;

    private MenuMetaBO meta;

    private String name;

    private String id;

    private Object[] children;

    private List<Object> childrenList;

    public MenuCategoryBO() {}

    public MenuCategoryBO(MenuBO menuBO) {

        this.path = menuBO.getPath();
        this.meta = new MenuMetaBO(menuBO);
        this.name = menuBO.getName();
        this.id = menuBO.getId();
        this.childrenList = new ArrayList<>();
    }
}