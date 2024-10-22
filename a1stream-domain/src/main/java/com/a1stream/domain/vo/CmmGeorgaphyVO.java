package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmGeorgaphyVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long geographyId;

    private String geographyClassificationId;

    private String geographyCd;

    private String geographyNm;

    private String geographyFullNm;

    private Long parentGeographyId;

}
