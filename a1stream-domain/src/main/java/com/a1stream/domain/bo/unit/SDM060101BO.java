package com.a1stream.domain.bo.unit;

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
public class SDM060101BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long taxId;
    private String cusTaxCode;
    private String cusTaxName;
    private String address;

    private String importFlag;
    private String errorMessage;
    transient List<String> error;
}