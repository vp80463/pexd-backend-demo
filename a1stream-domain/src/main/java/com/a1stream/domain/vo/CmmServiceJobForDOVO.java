package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmServiceJobForDOVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceJobForDoId;

    private Long productCategoryId;

    private String productCategoryCd;

    private String productCategoryNm;

    private Long jobId;

    private String jobCd;

    private String jobNm;

    private String jobRetrieve;

    private String serviceCategory;

    private BigDecimal manHours = BigDecimal.ZERO;

    private BigDecimal labourCost = BigDecimal.ZERO;

    private BigDecimal totalCost = BigDecimal.ZERO;
}
