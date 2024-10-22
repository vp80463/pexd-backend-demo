package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmWarrantyModelPartVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long warrantyModelPartId;

    private String warrantyType;

    private BigDecimal warrantyValue = BigDecimal.ZERO;

    private String modelCd;

    private Long modelId;

    private String partCd;

}
