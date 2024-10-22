package com.a1stream.domain.bo.service;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM020102FreeCouponBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long serviceDemandId;
    private String description;

    public SVM020102FreeCouponBO () {}

    public SVM020102FreeCouponBO(Long serviceDemandId, String description) {
        this.serviceDemandId = serviceDemandId;
        this.description = description;
    }
}