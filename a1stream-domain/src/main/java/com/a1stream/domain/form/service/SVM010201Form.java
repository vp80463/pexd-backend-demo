package com.a1stream.domain.form.service;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.service.BatteryBO;
import com.a1stream.domain.bo.service.JobDetailBO;
import com.a1stream.domain.bo.service.PartDetailBO;
import com.a1stream.domain.bo.service.SVM010201BO;
import com.a1stream.domain.bo.service.SituationBO;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:Service Order明细画面
*
* @author mid1341
*/
@Getter
@Setter
public class SVM010201Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private SVM010201BO orderInfo;

    private BaseTableData<SituationBO> situationList;
    private BaseTableData<JobDetailBO> jobList;
    private BaseTableData<PartDetailBO> partList;
    private List<BatteryBO> batteryList;
}
