package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class TaxAuthorityBO {

    /**
     * 帐户
     */
    private String Account;

    /**
     * ACpass
     */
    private String ACpass;

    /**
     * 用户名
     */
    private String username;

    /**
     * 通过
     */
    private String pass;

    /**
     * fkeys
     */
    private String fkeys;

    /**
     * 模式 SD
     */
    private String patternSD;

    /**
     * 模式 SPSV
     */
    private String patternSPSV;

    /**
     * 模式
     */
    private String pattern;
}