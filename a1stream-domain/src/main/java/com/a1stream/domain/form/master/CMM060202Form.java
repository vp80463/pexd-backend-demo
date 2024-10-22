package com.a1stream.domain.form.master;

import com.a1stream.common.model.BaseForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.master.CMM060202BO;
import com.a1stream.domain.bo.master.CMM060202Detail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM060202Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private CMM060202BO packageInfo;

    private BaseTableData<CMM060202Detail> categoryDetails;
    private BaseTableData<CMM060202Detail> serviceDetails;
    private BaseTableData<CMM060202Detail> partsDetails;
}