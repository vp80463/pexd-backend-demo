package com.a1stream.common.model;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValueListResultBO implements Serializable {

    private static final long serialVersionUID = -6152123301833655754L;

    private int total;
    private List<?> list;

    public ValueListResultBO() {}

    // pop-over
    public ValueListResultBO(List<?> list) {
        this.list = list;
    }

    // pop-up
    public ValueListResultBO(List<?> list, int total) {
        this.total = total;
        this.list = list;
    }
}