package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrivacyPolicyCVBO extends BaseCVBO {

    private static final long serialVersionUID = 1L;

    private String agreementResult;

    public PrivacyPolicyCVBO() {
    }
    public PrivacyPolicyCVBO(String agreementResult) {
        this.agreementResult = agreementResult;
    }
}
