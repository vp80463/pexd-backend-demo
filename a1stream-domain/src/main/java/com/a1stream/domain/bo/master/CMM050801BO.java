package com.a1stream.domain.bo.master;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM050801BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long seasonIndexBatchId;

    private Long seasonIndexManualId;

    private Long productCategoryId;

    private Long facilityId;

    private String facility;

    private Long pointId;

    private Long largeGroupId;

    private String largeGroup;

    private String largeGroupCd;

    private String largeGroupNm;

    private String manualFlag;

    private BigDecimal jan;

    private BigDecimal feb;

    private BigDecimal mar;

    private BigDecimal apr;

    private BigDecimal may;

    private BigDecimal jun;

    private BigDecimal jul;

    private BigDecimal aug;

    private BigDecimal sep;

    private BigDecimal oct;

    private BigDecimal nov;

    private BigDecimal dec;

    private BigDecimal month01;

    private BigDecimal month02;

    private BigDecimal month03;

    private BigDecimal month04;

    private BigDecimal month05;

    private BigDecimal month06;

    private BigDecimal month07;

    private BigDecimal month08;

    private BigDecimal month09;

    private BigDecimal month10;

    private BigDecimal month11;

    private BigDecimal month12;

    private BigDecimal monthTotal;

}