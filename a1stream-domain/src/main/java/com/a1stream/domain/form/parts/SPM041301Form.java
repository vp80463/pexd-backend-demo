package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.SPM041301BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM041301Form extends BaseForm{

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String pointCd;

    private String pointNm;

    private String memo;

    private List<SPM041301BO> gridDataList;

    private List<SPM041301BO> importList;

}
