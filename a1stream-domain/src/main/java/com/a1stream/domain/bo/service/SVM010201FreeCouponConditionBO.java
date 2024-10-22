package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010201FreeCouponConditionBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String warrantyType;
    private String warrantyConditionNow;
    private String evFlag;
    private List<SVM010201FreeCouponDetailBO> freeCouponResult = new ArrayList<>();
}
