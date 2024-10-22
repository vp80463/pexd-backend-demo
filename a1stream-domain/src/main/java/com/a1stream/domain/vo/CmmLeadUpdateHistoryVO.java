package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmLeadUpdateHistoryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long leadUpdateHistoryId;

    private String dealerCd;

    private String telephone;

    private String customerNm;

    private String callDateByDealer;

    private String contactStatus;

    private String mcFlag;

    private String oilFlag;

    private Long leadManagementResultId;

    private String leadStatus;

    private String frameNo;

    private String plateNo;


}
