package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM040201BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String sectionCd;
    private String sectionNm;
    private String faultSectionCd;
    private String faultSectionNm;
    private Long sectionId;
}