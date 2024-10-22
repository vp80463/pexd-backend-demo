package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM0130PrintServiceJobBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serviceJob;

    private String serviceJobName;

    private BigDecimal stdmenhour;

    private String serviceCategoryId;

    private String serviceCategory;

    private String serviceDemandContent;

    private String jobName;

    private BigDecimal discount;

    private BigDecimal specialPrice;

    private BigDecimal amount;

}
