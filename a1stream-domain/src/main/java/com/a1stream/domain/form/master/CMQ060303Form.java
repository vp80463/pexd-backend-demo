package com.a1stream.domain.form.master;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMQ060303Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String campaignNo;
    private String campaignTitle;
    private String campaignType;
    private String campaignDescription;
    private String effectiveDate;
    private String expiredDate;
    private Long campaignId;
}
