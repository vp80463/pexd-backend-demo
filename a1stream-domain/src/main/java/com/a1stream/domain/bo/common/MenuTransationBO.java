package com.a1stream.domain.bo.common;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuTransationBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String path;

    private MenuMetaBO meta;

    private String name;

    private String id;

    public MenuTransationBO() {}

    public MenuTransationBO(MenuBO menuBO) {

        this.path = menuBO.getPath();
        this.meta = new MenuMetaBO(menuBO);
        this.name = menuBO.getName();
        this.id = menuBO.getId();
    }
}