package com.a1stream.domain.form.master;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.master.CMM050901BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM050901Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String point;

    private Long partsId;

    private String newPart;

    private String updateSign;

    private BaseTableData<CMM050901BO> partsDemandData;

    private List<CMM050901BO> importList;

    private Object otherProperty;

    private boolean pageFlg = true;

}
