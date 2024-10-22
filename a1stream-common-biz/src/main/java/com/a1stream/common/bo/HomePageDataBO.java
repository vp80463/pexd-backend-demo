package com.a1stream.common.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class HomePageDataBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6177315040613464266L;

    /**
     * 通知公告紧急标识
     */
    private Boolean notifyEmergentFlag = true;

    /**
     * 轮播图路径List
     */
    private List<HomePageAnnounceBO> imgUrlList;

    /**
     * 常用菜单list
     */
    private List<HomePageCmmMenuBO> commonMenuList;

    /**
     * 待办事项list
     */
    private List<HomePageToDoBO> toDoList;

    /**
     * 预约提醒
     */
    private HomePageReservationRemindBO reservationRemindBO;

    /**
     * 提醒消息list
     */
    private List<HomePageRemindMessageBO> importantRemindList;
}