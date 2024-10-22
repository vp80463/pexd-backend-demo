package com.a1stream.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.a1stream.common.constants.CommonConstants;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CmmEmployeeInstructionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long cmmEmployeeInstructionId;

    private String siteId;

    private Long customerId;

    private Long facilityId;

    private String orderNo;

    private Long consumerId;

    private String consumerCd;

    private String consumerNm;

    private String mobilePhone;

    private String orderStatus;

    private String modelCd;

    private String modelNm;

    private String frameNo;

    private String engineNo;

    private String warrantyNo;

    private String colorNm;

    private Long serializedProductId;

    private BigDecimal taxRate = BigDecimal.ZERO;

    private String custaxCd;

    private String modelType;

    private Integer displacement = CommonConstants.INTEGER_ZERO;

    private String paymentMethod;

    private String email;

    private String employeeCd;

    private String employeeNm;

    private String orderMonth;

    private Long cmmProductId;

    private Long localOrderId;

    private String orderDate;

    private String purchaseType;

    private BigDecimal employeeDiscount = BigDecimal.ZERO;

    private String lastNm;

    private String middleNm;

    private String firstNm;

    private String sns;

    private String gender;

    private Long provinceGeographyId;

    private String provinceGeographyNm;

    private Long cityGeographyId;

    private String cityGeographyNm;

    private String address;

    private String birthDate;

    private String birthYear;

    private Integer updateCount = 0;

    private Integer updateCounter;

    private String lastUpdatedBy;

    private LocalDateTime lastUpdated;

    private String createdBy;

    private LocalDateTime dateCreated;

    private String updateProgram;
}
