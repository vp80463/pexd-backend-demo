package com.a1stream.domain.bo.unit;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050501PrintBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String dealerCode;

    private String dealerName;

    private String promotionName;

    private String giftName;

    private String customer;

    private String modelName;

    private String frameNo;

}
