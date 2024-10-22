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
public class HomePageRemindMessageBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1301851143686437769L;

    /**
     * 提醒消息
     */
    private String message;

    /**
     * 提醒消息数量
     */
    private int count;

    /**
     * 提醒消息时间
     */
    private String date;
}