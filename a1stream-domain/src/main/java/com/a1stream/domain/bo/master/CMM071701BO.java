package com.a1stream.domain.bo.master;

import java.math.BigDecimal;
import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM071701BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long doId;
    private String modelTypeCd;
    private Long productCategoryId;
    private String jobCd;
    private String jobNm;
    private Long jobId;
    private BigDecimal laborHours;
    private BigDecimal laborCost;
    private BigDecimal totalCost;
    private Integer seqNo;
    private String errorMessage;
    transient List<String> error;
    transient List<Object[]> errorParam;
    transient List<String> warning;
    transient List<Object[]> warningParam;
}