package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmPdiSettingVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long pdiSettingId;

    private Long pdiId;

    private String itemCd;

    private String description;


}
