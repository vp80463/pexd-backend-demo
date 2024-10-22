package com.a1stream.domain.bo.service;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM0102PrintServiceHistoryBO implements Serializable {

    private static final long serialVersionUID = 1L;

    //serviceHistory
    private String orderDate;

    private String orderNo;

    private String serviceCategory;

    private String description;

}
