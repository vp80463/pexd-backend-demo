package com.a1stream.domain.form.unit;

import java.util.List;

/**
*
* 功能描述:
*
* @author mid2215
*/
import com.a1stream.common.model.BaseForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.unit.SDM060101BO;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SDM060101Form extends BaseForm{

    private static final long serialVersionUID = 1L;

    private Long taxId;
    private String cusTaxCode;
    private String cusTaxName;
    private String address;
    private boolean pageFlg = true;

    private BaseTableData<SDM060101BO> cusTaxData;

    private List<SDM060101BO> importList;

    private String importFlag;
}
