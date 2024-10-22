package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMQ070101BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String userCd;
    private String registerDate;
    private String dateFrom;
    private String dateTo;
    private String status;
    private String userId;
}
