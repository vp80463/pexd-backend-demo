package com.a1stream.domain.bo.unit;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010402ConsumerInfoBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long consumerId;

    private String relationType;

    private String consumer;

    private String firstNm;

    private String middleNm;

    private String lastNm;

    private String province;

    private String city;

    private String gender;

    private String telephone;

    private String mobilePhone;

    private String address;

    private String address2;

}
