package com.a1stream.common.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuCheckBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;

    private boolean permission = true;

    public MenuCheckBO() { }

    public MenuCheckBO(boolean permission, String message) {
        this.message = message;
        this.permission = permission;
    }
}