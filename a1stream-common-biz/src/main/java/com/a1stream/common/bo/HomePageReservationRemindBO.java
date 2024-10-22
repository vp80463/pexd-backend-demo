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
public class HomePageReservationRemindBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6808736628046395566L;

    /**
     * 今日确认
     */
    private Integer todayConfirmed;

    /**
     * 明日确认
     */
    private Integer tomorrowConfirmed;

    /**
     * 今日待确认
     */
    private Integer todayWaitConfirmed;

    /**
     * 明日待确认
     */
    private Integer tomorrowWaitConfirmed;

    /**
     * 今天
     */
    private String today;

    /**
     * 明天
     */
    private String tomorrow;

    /**
     * 待确认
     */
    private String waitType;

    /**
     * 确认
     */
    private String confirmType;
}