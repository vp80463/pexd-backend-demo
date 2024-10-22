package com.a1stream.domain.bo.unit;

import java.math.BigDecimal;
import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Getter
@Setter
public class CMM090101BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long productId;
    private String modelCode;
    private String modelName;
    private String registrationDate;
    private String expiredDate;
    private BigDecimal price;

    private String errorMessage;
    private String warningMessage;
    transient List<String> error;
    transient List<Object[]> errorParam;

}