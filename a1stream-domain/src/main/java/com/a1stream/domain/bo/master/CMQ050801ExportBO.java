package com.a1stream.domain.bo.master;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;
import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMQ050801ExportBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    @ExcelProperty(index = 0)
    private String partsCd;

    @ExcelProperty(index = 1)
    private String demandSource;

    @ExcelProperty(index = 2)
    private String largeGroupCd;

    @ExcelProperty(index = 3)
    private String middleGroupCd;

    @ExcelProperty(index = 4)
    private String costUsage;

    @ExcelProperty(index = 5)
    private BigDecimal jOne;

    @ExcelProperty(index = 6)
    private BigDecimal jTwo;

    @ExcelProperty(index = 7)
    private BigDecimal trendIndex;

    @ExcelProperty(index = 8)
    private BigDecimal seasonIndex;

    @ExcelProperty(index = 9)
    private BigDecimal demandForecast;

    @ExcelProperty(index = 10)
    private BigDecimal rop;

    @ExcelProperty(index = 11)
    private BigDecimal roq;

    @ExcelProperty(index = 12)
    private String manualSign;

    @ExcelProperty(index = 13)
    private BigDecimal boQty;

    @ExcelProperty(index = 14)
    private BigDecimal onHandQty;

    @ExcelProperty(index = 15)
    private BigDecimal totalStock;

    @ExcelProperty(index = 16)
    private BigDecimal futureStock;

    @ExcelProperty(index = 17)
    private BigDecimal n0;

    @ExcelProperty(index = 18)
    private BigDecimal n1;

    @ExcelProperty(index = 19)
    private BigDecimal n2;

    @ExcelProperty(index = 20)
    private BigDecimal n3;

    @ExcelProperty(index = 21)
    private BigDecimal n4;

    @ExcelProperty(index = 22)
    private BigDecimal n5;

    @ExcelProperty(index = 23)
    private BigDecimal n6;

    @ExcelProperty(index = 24)
    private BigDecimal n7;

    @ExcelProperty(index = 25)
    private BigDecimal n8;

    @ExcelProperty(index = 26)
    private BigDecimal n9;

    @ExcelProperty(index = 27)
    private BigDecimal n10;

    @ExcelProperty(index = 28)
    private BigDecimal n11;

    @ExcelProperty(index = 29)
    private BigDecimal n12;

    @ExcelProperty(index = 30)
    private BigDecimal n13;

    @ExcelProperty(index = 31)
    private BigDecimal n14;

    @ExcelProperty(index = 32)
    private BigDecimal n15;

    @ExcelProperty(index = 33)
    private BigDecimal n16;

    @ExcelProperty(index = 34)
    private BigDecimal n17;

    @ExcelProperty(index = 35)
    private BigDecimal n18;

    @ExcelProperty(index = 36)
    private BigDecimal n19;

    @ExcelProperty(index = 37)
    private BigDecimal n20;

    @ExcelProperty(index = 38)
    private BigDecimal n21;

    @ExcelProperty(index = 39)
    private BigDecimal n22;

    @ExcelProperty(index = 40)
    private BigDecimal n23;

    @ExcelProperty(index = 41)
    private BigDecimal n24;

}