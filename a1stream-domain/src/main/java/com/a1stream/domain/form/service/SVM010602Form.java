package com.a1stream.domain.form.service;

import com.a1stream.common.model.BaseForm;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class SVM010602Form extends BaseForm {

    @Serial
    private static final long serialVersionUID = -8797286449888810643L;

    /**
     * 通知类型
     */
    private String remindType;

    /**
     * 通知类型id
     */
    private String remindTypeId;

    /**
     * 提醒日期
     */
    private String remindDate;

    /**
     * 车型id
     */
    private Long modelId;

    /**
     * 是否显示已提醒
     */
    private String showReminded;

    /**
     * 车牌号
     */
    private String plateNo;

    /**
     * 仓库id
     */
    private Long pointId;

    /**
     * 客户id
     */
    private Long consumerId;

    /**
     * 序列化产品id
     */
    private Long serializedProductId;

    /**
     * 主题
     */
    private String subject;

    /**
     * 满意度
     */
    private String satisfactionPoint;

    /**
     * 拒绝电话
     */
    private String refuseCall;

    /**
     * 描述
     */
    private String description;

    /**
     * 提醒计划id
     */
    private Long remindScheduleId;
}
