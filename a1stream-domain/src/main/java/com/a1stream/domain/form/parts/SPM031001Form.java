package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.SPM031001BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM031001Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String point;

    private String pointCd;

    private String pointNm;

    private List<SPM031001BO> gridDataList;
}
