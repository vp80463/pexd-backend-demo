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
public class SVM010602BO extends BaseBO {

    @Serial
    private static final long serialVersionUID = 4820449644847940239L;

    /**
     * 提醒时间
     */
    private String recordDate;

    /**
     * 最后更新人
     */
    private String lastUpdatedBy;

    /**
     * 主题
     */
    private String subject;
}