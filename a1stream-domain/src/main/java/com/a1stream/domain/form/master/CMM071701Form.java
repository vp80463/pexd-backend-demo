package com.a1stream.domain.form.master;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.master.CMM071701BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM071701Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String modelTypeCd;
    private Long modelTypeId;
    private Boolean errorExistFlag = false;
    private List<CMM071701BO> importList;
    private List<CMM071701BO> gridDataList;
    private BaseTableData<CMM071701BO> updateGridDataList;
}