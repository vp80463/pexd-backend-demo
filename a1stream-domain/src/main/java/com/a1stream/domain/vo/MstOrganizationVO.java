package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class MstOrganizationVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long organizationId;

    private Long cmmOrganizationId;

    private String organizationCd;

    private String organizationNm;

    private String organizationAbbr;

    private String organizationRetrieve;

    private Long provinceId;

    private String provinceNm;

    private Long cityId;

    private String cityNm;

    private String address1;

    private String address2;

    private String postCode;

    private String businessType;

    private String fromDate;

    private String toDate;

    private String picId;

    private String deleteFlag;

    private String contactTel;

    private String contactFax;

    private String contactPic;

    private String contactMail;

}
