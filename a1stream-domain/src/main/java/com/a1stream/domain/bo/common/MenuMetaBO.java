package com.a1stream.domain.bo.common;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuMetaBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer rank;

    private String title;
    
    private boolean showLink;
    
    private String activePath;

    public MenuMetaBO() {}

    public MenuMetaBO(MenuBO menuBO) {

        this.rank = menuBO.getRank();
        this.title = menuBO.getTitle();
        this.showLink = menuBO.isShowLink();
        this.activePath = menuBO.getActivePath();
    }
}