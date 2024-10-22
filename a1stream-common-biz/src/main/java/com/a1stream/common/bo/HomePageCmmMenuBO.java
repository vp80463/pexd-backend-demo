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
public class HomePageCmmMenuBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9086820242297842172L;

    /**
     * 菜单id
     */
    private String menuId;

    /**
     * 菜单cd
     */
    private String menuCd;

    /**
     * 菜单名称
     */
    private String menuNm;

    /**
     * 菜单url
     */
    private String linkUrl;

    /**
     * 菜单图片
     */
    private String menuPic;
}