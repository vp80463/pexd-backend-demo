package com.a1stream.domain.form.service;

import java.util.List;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM020201Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String fixDate;

    private String fixDateTo;

    private String requestCategory;

    private List<String> status;

    private String paymentControlNo;
}