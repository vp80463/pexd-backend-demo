package com.a1stream.domain.bo.common;

import com.a1stream.domain.vo.CmmConsumerVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmmConsumerBO extends CmmConsumerVO {

    private static final long serialVersionUID = 1L;

    private String mobilePhone;

    private String mobilePhone2;

    private String mobilePhone3;

    private String privacyPolicyResult;

    private String age;
}