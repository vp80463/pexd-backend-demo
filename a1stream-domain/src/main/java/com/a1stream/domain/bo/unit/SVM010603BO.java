package com.a1stream.domain.bo.unit;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010603BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    //headerData
    private String contactStatus;
    private String remark;
    private String leadStatus;
    private String leadType;
    private String phoneNo;
    private String customerNm;
    private String interestedModel;
    private String interestedColor;
    private String provinceNm;
    private String cityNm;
    private String contackDateFromCustomer;
    private String dealerCallDate;
    private String estimateTimeToBuy;
    private String dataSource;
    private Long leadManagementResultId;
    private Long no;

    //footerData
    private String detailPhoneNo;
    private String detailCustomerNm;
    private String detailDealerCallDate;
    private String detailContactStatus;

}
