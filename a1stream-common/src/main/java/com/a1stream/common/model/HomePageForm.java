package com.a1stream.common.model;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class HomePageForm extends BaseForm{

    @Serial
    private static final long serialVersionUID = 8636648901103569432L;

    /**
     * 类型
     */
    private String businessType;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 混淆名称
     */
    private String confusionName;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 站点id
     */
    private String baseSiteId;

    /**
     * 真实站点id
     */
    private String realSiteId;

    /**
     * 用户习惯类型id
     */
    private String userHabitTypeId;

    /**
     * 通知类型id
     */
    private List<String> notifyTypeIdList = new ArrayList<>();

    /**
     * 当前日期
     */
    private String nowDate;

    /**
     * 通知公告紧急标识
     */
    private Boolean notifyEmergentFlag = true;

    /**
     * 个人 ID
     */
    private Long pointId;

    private String language;

    /**
     * 菜单名称
     */
    private String menuCd;

    /**
     * 消息提醒ID
     */
    private List<Long> messageRemindIdList;
}