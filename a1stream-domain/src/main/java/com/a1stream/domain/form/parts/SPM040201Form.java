package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.parts.SPM040201BO;
import com.a1stream.domain.bo.parts.SPM040202BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM040201Form extends BaseForm{

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private String point;
    private Long partsId;
    private String part;
    private Long largeGroupId;

    private BaseTableData<SPM040201BO> ropRoqData;

    private List<SPM040202BO> importList;

    private Object otherProperty;

}
