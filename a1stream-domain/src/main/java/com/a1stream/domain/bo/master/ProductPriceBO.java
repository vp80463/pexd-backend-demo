package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class ProductPriceBO extends BaseBO {

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

    /**
     * 编号
     */
    private String testF;

    /**
     * 编号
     */
    private String testG;

    /**
     * 编号
     */
    private String testH;

    /**
     * 编号
     */
    private String testI;

    /**
     * 编号
     */
    private String testJ;

    /**
     * 编号
     */
    private String testK;

    /**
     * 编号
     */
    private String testL;

    /**
     * 编号
     */
    private String testM;

    /**
     * 编号
     */
    private String testN;

    /**
     * 编号
     */
    private String testO;

    /**
     * 编号
     */
    private String testP;

    /**
     * 编号
     */
    private String testQ;

    /**
     * 编号
     */
    private String testR;

    /**
     * 编号
     */
    private String testS;

    /**
     * 编号
     */
    private String testT;

    /**
     * 编号
     */
    private String testU;

    /**
     * 编号
     */
    private String testV;

    /**
     * 编号
     */
    private String testW;

    /**
     * 编号
     */
    private String testX;

    /**
     * 编号
     */
    private String testY;

    /**
     * 编号
     */
    private String testZ;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 警告信息
     */
    private String warningMessage;

    transient List<String> error;

    transient List<Object[]> errorParam;

    transient List<String> warning;

    transient List<Object[]> warningParam;
}