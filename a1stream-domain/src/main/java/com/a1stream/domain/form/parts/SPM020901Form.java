package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.SPM020901BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM020901Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String pointCd;

    private String pointNm;

    private String pickingSeqNo;

    private String duNo;

    private String backFlag;

    private Long orderId;

    private List<SPM020901BO> allTableDataList;

    private Integer piUpdateCount;
}
