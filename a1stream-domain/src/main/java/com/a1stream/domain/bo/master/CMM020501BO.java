package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM020501BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String pointCd;
    private String pointNm;
    private String wsDealerSign;
    private String shop;
    private String area;
    private String telephone;
    private String effectiveDate;
    private String expiredDate;
    private Long pointId;
}
