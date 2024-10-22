package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ApiDealerSettingVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long dlApiSettingId;

    private String dealerCd;

    private Long apiInfoId;

    private String comment;


}
