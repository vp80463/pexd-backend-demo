package com.a1stream.domain.bo.master;

import java.math.BigDecimal;
import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM050901BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String dealerCode;
    private String pointCode;
    private Long pointId;
    private String partsNo;
    private Long partsId;
    private BigDecimal n0;
    private BigDecimal n1;
    private BigDecimal n2;
    private BigDecimal n3;
    private BigDecimal n4;
    private BigDecimal n5;
    private BigDecimal n6;
    private BigDecimal n7;
    private BigDecimal n8;
    private BigDecimal n9;
    private BigDecimal n10;
    private BigDecimal n11;
    private BigDecimal n12;
    private BigDecimal n13;
    private BigDecimal n14;
    private BigDecimal n15;
    private BigDecimal n16;
    private BigDecimal n17;
    private BigDecimal n18;
    private BigDecimal n19;
    private BigDecimal n20;
    private BigDecimal n21;
    private BigDecimal n22;
    private BigDecimal n23;
    private BigDecimal n24;

    transient List<String> error;
    transient List<Object[]> errorParam;
    transient List<String> warning;
    transient List<Object[]> warningParam;

    private String errorMessage;
    private String warningMessage;

}