package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class EInvoiceBO {

    /**
     * 账户
     */
    private String Account;

    /**
     * ACpss
     */
    private String ACpass;

    /**
     * XML INV 数据
     */
    private String xmlInvData;

    /**
     * 用户名
     */
    private String username;

    /**
     * 通过
     */
    private String pass;

    /**
     * 模式 SD
     */
    private String patternSD;

    /**
     * 模式 SPSV
     */
    private String patternSPSV;

    /**
     * 串行SD
     */
    private String serialSD;

    /**
     * 串行 SPSV
     */
    private String serialSPSV;

    /**
     * 转换
     */
    private int convert;

    /**
     * 模式
     */
    private String pattern;

    /**
     * 串行
     */
    private String serial;
}