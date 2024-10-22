package com.a1stream.domain.form.service;

import java.util.List;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010701Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String siteId;
    private String dateFrom;
    private String dateTo;
    private String plateNo;
    private Long pointId;
    private String consumerNm;
    private String mobilePhone;
    private List<String> reservationSts;
    private boolean exportFlag;
}
