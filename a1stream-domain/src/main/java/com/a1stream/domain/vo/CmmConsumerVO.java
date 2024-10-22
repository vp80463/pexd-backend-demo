package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.a1stream.common.utils.ComUtil;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmConsumerVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long consumerId;

    private String consumerType;

    private String vipNo;

    private String lastNm;

    private String middleNm;

    private String firstNm;

    private String consumerFullNm;

    private String consumerRetrieve;

    private String gender;

    private String idNo;

    private String idClassificationNo;

    private String visaNo;

    private String email;

    private String email2;

    private String telephone;

    private String faxNo;

    private String postCode;

    private String taxCode;

    private Long provinceGeographyId;

    private Long cityGeographyId;

    private String address;

    private String address2;

    private String occupation;

    private String interestModel;

    private String mcBrand;

    private String mcPurchaseDate;

    private String comment;

    private String registrationDate;

    private String registrationReason;

    private String sns;

    private String birthDate;

    private String birthYear;

    private String businessNm;

    private String mobilePhone;

    public static CmmConsumerVO create(String consumerType) {
        CmmConsumerVO entity = new CmmConsumerVO();
        entity.setConsumerId(IdUtils.getSnowflakeIdWorker().nextId());
        entity.setRegistrationDate(ComUtil.nowLocalDate());
        entity.setConsumerType(consumerType);

        return entity;
    }
}
