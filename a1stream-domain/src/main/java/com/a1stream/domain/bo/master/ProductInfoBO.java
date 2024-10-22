package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class ProductInfoBO extends BaseBO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    private String categoryCd;

    /**
     * 名称
     */
    @ExcelProperty(index = 0)
    private String categoryNm;

    /**
     * 单价
     */
    private BigDecimal goodsPrice;

    /**
     * 分销指导价
     */
    private BigDecimal guidePrice;

    /**
     * 最高限度价
     */
    private BigDecimal maxPrice;

    /**
     * 编号
     */
    private String testA;

    /**
     * 编号
     */
    private String testB;

    /**
     * 编号
     */
    private String testC;

    /**
     * 编号
     */
    private String testD;

    /**
     * 编号
     */
    private String testE;
}