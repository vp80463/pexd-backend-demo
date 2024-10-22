package com.a1stream.domain.bo.service;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM020202BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String category;

    private String requestNo;

    private String requestDate;

    private BigDecimal paymentAmount;

    private String requestComment;

    private Long serviceRequestId;
}