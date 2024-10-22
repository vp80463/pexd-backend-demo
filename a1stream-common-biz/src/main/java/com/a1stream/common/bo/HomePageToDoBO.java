package com.a1stream.common.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class HomePageToDoBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4908124003213399830L;

    /**
     * 菜单编号
     */
    private String menuCd;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 名称
     */
    private String name;

    /**
     * 排序类型
     */
    private String orderType;
}