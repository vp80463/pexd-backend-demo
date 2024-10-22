package com.a1stream.domain.vo;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmLeadManagement2sVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long leadManagementResultId;

    private String facilityCd;

    private String scoringDataExportDate;

    private String leadStatus;

    private String lastNm;

    private String middleNm;

    private String firstNm;

    private String province;

    private String districtTownCity;

    private String mobilePhone;

    private String email;

    private String lastVisitDealerCd;

    private String lastVisitDealerNm;

    private String lastVisitDate;

    private String frameNo;

    private String plateNo;

    private String currentMcModel;

    private Integer fscUseHistory = CommonConstants.INTEGER_ZERO;

    private String currentVoucher;

    private String nextFscExpireDate;

    private String lastOilChangeDate;

    private String oilNm;

    private String contactStatus;

    private String callDateByDealer;

    private String note;

    private String oilFlag;

    private String timeStamp;

    private String interestedModel;

    private String interestedColor;


}
