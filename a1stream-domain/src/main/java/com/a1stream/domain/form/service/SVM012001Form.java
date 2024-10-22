package com.a1stream.domain.form.service;

import com.a1stream.common.model.BaseForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.service.SVM012001BO;
import com.a1stream.domain.bo.service.SVM012001JobBO;
import com.a1stream.domain.bo.service.SVM012001PartBO;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:Service Order Other Brand明细画面
*
* @author mid1341
*/
@Getter
@Setter
public class SVM012001Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private SVM012001BO orderInfo;

    private BaseTableData<SVM012001JobBO> jobList;
    private BaseTableData<SVM012001PartBO> partList;
}
