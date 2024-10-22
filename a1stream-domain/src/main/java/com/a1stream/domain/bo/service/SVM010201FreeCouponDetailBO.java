package com.a1stream.domain.bo.service;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010201FreeCouponDetailBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String couponName;
    private String couponResult;

    public SVM010201FreeCouponDetailBO () {}

    public SVM010201FreeCouponDetailBO(String couponName, String couponResult) {
        this.couponName = couponName;
        this.couponResult = couponResult;
    }
}
