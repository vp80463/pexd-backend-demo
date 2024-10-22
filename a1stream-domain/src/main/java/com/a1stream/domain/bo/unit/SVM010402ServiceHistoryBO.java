package com.a1stream.domain.bo.unit;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010402ServiceHistoryBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String orderDate;

    private String orderNo;

    private String serviceCategory;

    private String serviceTitle;

    private String serviceDemand;

}