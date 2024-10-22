package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmWarrantySerializedProductVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long warrantySerializedProductId;

    private Long productId;

    private Long serializedProductId;

    private String warrantyProductClassification;

    private String fromDate;

    private String toDate;

    private String warrantyProductUsage;

    private String comment;


}
