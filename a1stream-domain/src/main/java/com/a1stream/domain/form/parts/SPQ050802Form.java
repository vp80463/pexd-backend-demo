package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.SPQ050802BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ050802Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String pointCd;

    private String pointNm;

    private String targetMonth;

    private String largeGroupCd;

    private List<SPQ050802BO> gridDataList;
}
