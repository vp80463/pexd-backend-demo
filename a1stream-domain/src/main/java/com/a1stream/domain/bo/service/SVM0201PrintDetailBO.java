package com.a1stream.domain.bo.service;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM0201PrintDetailBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String partNo;

    private String partName;

    private BigDecimal qty;

}
