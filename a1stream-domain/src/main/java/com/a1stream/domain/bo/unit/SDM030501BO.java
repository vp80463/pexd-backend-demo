package com.a1stream.domain.bo.unit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM030501BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dealerCd;

    private String orderNo;
    
    private String orderStatus;

    private String modelCd;

    private String lastNm;

    private String middleNm;

    private String firstNm;

    private String mobilePhone;

    private String sns;

    private String gender;

    private String province;

    private String district;

    private String address;

    private String email;

    private String employeeCd;

    private String employeeNm;

    private String errorMessage;

    private String warningMessage;
    
    transient List<String> error;

    transient List<Object[]> errorParam;

    transient List<String> warning;

    transient List<Object[]> warningParam;

    private String purchaseType;

    private BigDecimal employeeDiscount;

    private String birthDay;

    private String birthYear;

    private String birthDate;

    private Long provinceId;

    private Long districtId;

    private Long cmmEmployeeInstructionId;

}
