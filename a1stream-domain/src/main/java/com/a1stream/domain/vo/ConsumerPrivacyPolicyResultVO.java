package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ConsumerPrivacyPolicyResultVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long consumerPrivacyPolicyResultId;

    private Long consumerId;

    private String consumerFullNm;

    private String mobilePhone;

    private String agreementResult;

    private String resultUploadDate;

    private String facilityCd;

    private String deleteFlag;

    private String consumerRetrieve;
}
