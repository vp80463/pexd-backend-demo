package com.a1stream.ifs.bo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvRegisterDocBO extends SvRegisterDocAbstractBO {

    private static final long serialVersionUID = 1L;

    private String pdiDate;
    private String pdiNumber;
    private String justificationComment;
    private String registrationDealerCode;
    private String dealerCode;

    public String getDealerCode() {
        return (this.dealerCode !=null && this.dealerCode.length()>0)
                 ? this.dealerCode
                 : this.getRegistrationDealerCode();
    }
}