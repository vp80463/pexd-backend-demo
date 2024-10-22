package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.parts.DIM020501BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DIM020501Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String pointCd;

    private Long workzoneId;

    private String workzoneCd;

    private String workzoneNm;

    private BaseTableData<DIM020501BO> tableDataList;

    private List<DIM020501BO> gridDataList;
}
