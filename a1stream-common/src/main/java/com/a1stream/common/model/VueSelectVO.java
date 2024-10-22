package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VueSelectVO {

    private String label;

    private Long value;

    public VueSelectVO() {}

    public VueSelectVO(String label, Long value) {

        this.label = label;
        this.value = value;
    }
}