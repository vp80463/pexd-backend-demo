package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.SPM030501BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM030501Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private Long partsId;

    private String pointvl;

    private String parts;

    private List<SPM030501BO> gridDataList;
}
