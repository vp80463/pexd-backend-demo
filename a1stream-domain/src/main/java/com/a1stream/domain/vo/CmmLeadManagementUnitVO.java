package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmLeadManagementUnitVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long leadManagementResultId;

    private String dealerCd;

    private String telephone;

    private String customerNm;

    private String province;

    private String districtTownCity;

    private String interestedModel;

    private String interestedColor;

    private String leadStatus;

    private String contactStatus;

    private String source;

    private String contactDateFromCustomer;

    private String callDateByDealer;

    private String timestamp;

    private String estimateTimeToBuy;

    private String remark;

    private String leadType;

}
