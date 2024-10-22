package com.a1stream.domain.form.master;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM010301Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    // Header
    private String lastNm;
    private String middleNm;
    private String firstNm;
    private String phone;
    private String idNo;
    private String vipNo;

    // Condition
    private String salesDateFrom;
    private String salesDateTo;
    private String serviceDateFrom;
    private String serviceDateTo;
    private Long modelId;
    private String consumerType;
    private String gender;
    private String birthday;
    private String ageDateFrom;
    private String ageDateTo;
    private Long province;
    private Long cityId;
    private String vipNoHolder;
    private String regDateFrom;
    private String regDateTo;

    // Distinguish Flag
    private boolean headerFlag;
}