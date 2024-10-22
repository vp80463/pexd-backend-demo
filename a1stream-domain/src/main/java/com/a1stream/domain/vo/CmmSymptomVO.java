package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmSymptomVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long symptomId;

    private String symptomCd;

    private String description;

    private Long productSectionId;
}
