package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.SPQ030801BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ030801Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String pointCd;

    private String pointNm;

    private String locationType;

    private String locationTypeId;

    private Long binTypeId;

    private String binType;

    private List<SPQ030801BO> gridDataList;
}
