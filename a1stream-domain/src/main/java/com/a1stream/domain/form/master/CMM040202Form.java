package com.a1stream.domain.form.master;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.vo.CmmSymptomVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM040202Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String siteId;
    private String sectionCd;
    private String sectionNm;
    private Long sectionId;

    private List<CmmSymptomVO> detailList;
    private BaseTableData<CmmSymptomVO> tableDataList;
}