package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.parts.SPM021402BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM021402Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String status;
    
    private Long returnRequestListId;

    private String selectPicking;

    private List<SPM021402BO> allTableDataList;

    private BaseTableData<SPM021402BO> tableDataList;

    private Long deliveryOrderId;
}
