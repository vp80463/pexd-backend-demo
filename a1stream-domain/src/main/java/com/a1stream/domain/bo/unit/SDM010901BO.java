package com.a1stream.domain.bo.unit;

import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM010901BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String code;

    private String name;

    private String desc;

    private String dealerCd;

    private String pointCd;

    private String date;

    private String frameNo;

    private Long productId;

    private String productCd;

    private Long serializedProductId;

    private String errorMessage;

    private String warningMessage;
    
    transient List<String> error;

    transient List<Object[]> errorParam;

    transient List<String> warning;

    transient List<Object[]> warningParam;

}
