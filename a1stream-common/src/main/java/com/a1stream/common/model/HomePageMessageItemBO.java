package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class HomePageMessageItemBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4666865472183143242L;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 描述
     */
    private String description;

    /**
     * 标题
     */
    private String title;

    /**
     * 时间
     */
    private String datetime;

    /**
     * 阅读状态
     */
    private String readType;

    /**
     * 消息提醒ID
     */
    private Long messageRemindId;

    /**
     * 阅读类别
     */
    private String readCategoryType;
}