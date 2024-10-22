package com.a1stream.domain.form.unit;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.unit.SVM010603BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010603Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String dateFrom;

    private String dateTo;

    private String model;

    private Long modelId;

    private String modelCd;

    private String phoneNo;

    private Long leadManagementResultId;

    private List<String> status;

    private List<SVM010603BO> gridData;
}
