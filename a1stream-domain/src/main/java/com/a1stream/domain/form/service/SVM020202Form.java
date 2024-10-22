package com.a1stream.domain.form.service;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.service.SVM020202BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM020202Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private List<SVM020202BO> gridDataList;

    private String bulletinNo;

    private String vatCd;

    private String invoiceNo;

    private String invoiceDate;

    private String serialNo;

    private Long paymentId;

    private String category;

    private String siteId;

    private String paymentMonth;

    private String paymentCategory;
}