package com.a1stream.domain.form.service;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.service.SVM020101BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM020101Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String siteId;
    private Long pointId;
    private String dateFrom;
    private String dateTo;
    private String serviceOrderNo;
    private Long requestCategoryId;
    private List<String> status;
    private String allFlag;

    private List<SVM020101BO> tableDataList;
}