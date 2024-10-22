package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmMstAcctMonthVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long acctMonthId;

    private String accountMonth;

    private String fromDate;

    private String toDate;


}
