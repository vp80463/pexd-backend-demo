package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class MstFacilityVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long facilityId;

    private String facilityCd;

    private String facilityNm;

    private String facilityAbbr;

    private String facilityRetrieve;

    private String numberingIdCd;

    private String spPurchaseFlag;

    private String facilityRoleType;

    private Long provinceId;

    private String provinceNm;

    private Long cityId;

    private String cityNm;

    private String address1;

    private String address2;

    private String postCode;

    private String fromDate;

    private String toDate;

    private String picId;

    private String deleteFlag;

    private String doFlag;

    private String newFlag;

    private String multiAddressFlag;

    private String contactTel;

    private String contactFax;

    private String contactPic;

    private String contactMail;

    private String shopFlag;

    private String warehouseFlag;
}
