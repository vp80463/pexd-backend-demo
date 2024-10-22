package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.DIM020701BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DIM020701Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String pointCd;

    private Object otherProperty;

    private Boolean checkFlag;

    private Boolean errorExistFlag = false;

    private List<DIM020701BO> gridDataList;

    private List<DIM020701BO> importList;
}
