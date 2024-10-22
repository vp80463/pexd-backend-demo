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
public class SVM010601Form extends BaseForm {

    @Serial
    private static final long serialVersionUID = 7725468493862151504L;

    /**
     * 通知类型
     */
    private String remindType;

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
     * 仓库cd
     */
    private String pointCd;
}
