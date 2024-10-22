package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM020201BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String personId;
    private String personCd;
    private String personNm;
    private String systemUser;
    private String userCd;
    private String fromDate;
    private String toDate;
}