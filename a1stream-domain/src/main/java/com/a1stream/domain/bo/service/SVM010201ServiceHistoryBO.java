package com.a1stream.domain.bo.service;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010201ServiceHistoryBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String siteId;
    private String orderDate;
    private String orderNo;
    private String serviceCategory;
    private String serviceTitle;
}
