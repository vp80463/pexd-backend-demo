package com.a1stream.domain.bo.service;

import com.a1stream.common.model.BaseBO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class SVM010601BO extends BaseBO {

    @Serial
    private static final long serialVersionUID = -4971970001635019805L;

    /**
     * 拒绝电话
     */
    private String refuseCall;

    /**
     * 拒绝电话标识
     */
    private boolean refuseCallFlag;

    /**
     * 提醒标志
     */
    private boolean remindFlag;

    /**
     * 提醒标志
     */
    private String remindFlagStr;

    /**
     * 提醒类型
     */
    private String remindType;

    /**
     * 提醒类型id
     */
    private String remindTypeId;

    /**
     * 提醒日期
     */
    private String validDate;

    /**
     * 失效日期
     */
    private String invalidDate;

    /**
     * 描述
     */
    private String description;

    /**
     * 车主姓名
     */
    private String consumerName;

    /**
     * 车主电话
     */
    private String mobilePhone;

    /**
     * 车牌号
     */
    private String plateNo;

    /**
     * 车型id
     */
    private Long modelId;

    /**
     * 车型
     */
    private String model;

    /**
     * 仓库id
     */
    private Long pointId;

    /**
     * 仓库cd
     */
    private String pointCd;

    /**
     * 车主id
     */
    private Long consumerId;

    /**
     * 序列化产品id
     */
    private Long serializedProductId;

    /**
     * 提醒计划id
     */
    private Long remindScheduleId;

    /**
     * 提醒日期
     */
    private String remindedDate;

    /**
     * 提醒主题
     */
    private String subject;
}