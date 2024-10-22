package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmSpecialCompanyTaxVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long specialCompanyTaxId;

    private String specialCompanyTaxCd;

    private String specialCompanyTaxNm;

    private String address;


}
