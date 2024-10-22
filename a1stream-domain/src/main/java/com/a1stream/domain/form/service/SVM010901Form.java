package com.a1stream.domain.form.service;

import com.a1stream.common.model.BaseForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.service.SVM010901BO;
import com.a1stream.domain.bo.service.SituationBO;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:Service Order For Other Brand
*
* @author mid1341
*/
@Getter
@Setter
public class SVM010901Form extends BaseForm {

    private static final long serialVersionUID = 1L;
    
    private String action;

    private SVM010901BO orderInfo;

    /**
     * Grid
     */
    private BaseTableData<SituationBO> situations;
}
