package com.a1stream.domain.parameter.service;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumerPolicyParameter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String siteId;
	private Long consumerId;
    private String pointCd;
	private String lastNm;
	private String middleNm;
	private String firstNm;
	private String mobilephone;
	private String email;
	private String policyResultFlag;
    private String policyFileName;

    public ConsumerPolicyParameter() {}

	public ConsumerPolicyParameter(Long consumerId, String siteId
			, String lastNm, String middleNm, String firstNm
			, String mobilephone, String email) {

		this.consumerId = consumerId;
		this.siteId = siteId;
		this.lastNm = lastNm;
		this.middleNm = middleNm;
		this.firstNm = firstNm;
		this.mobilephone = mobilephone;
		this.email = email;
	}

	public ConsumerPolicyParameter(String siteId, String pointCd, String lastNm, String middleNm, String firstNm,
			String mobilephone, String policyResultFlag, String policyFileName) {

		this.siteId = siteId;
		this.pointCd = pointCd;
		this.lastNm = lastNm;
		this.middleNm = middleNm;
		this.firstNm = firstNm;
		this.mobilephone = mobilephone;
		this.policyResultFlag = policyResultFlag;
		this.policyFileName = policyFileName;
	}
}
