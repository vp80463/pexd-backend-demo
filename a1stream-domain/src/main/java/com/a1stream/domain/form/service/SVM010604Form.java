package com.a1stream.domain.form.service;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.service.SVM010604BO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.List;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class SVM010604Form extends BaseForm {

    @Serial
    private static final long serialVersionUID = 1189023931294415396L;

    /**
     * 评分日期开始
     */
    private String scoringDateFrom;

    /**
     * 评分日期结束
     */
    private String scoringDateTo;

    /**
     * 评分
     */
    private String scoring;

    /**
     * 分类
     */
    private String category;

    /**
     * 营销结果
     */
    private List<String> telemarketingResultList;

    /**
     * 仓库id
     */
    private Long pointId;

    /**
     * 仓库cd
     */
    private String pointCd;

    /**
     * 手机号
     */
    private String mobilePhone;

    /**
     * 营销结果id
     */
    private Long leadManagementResultId;

    /**
     * 提交列表
     */
    private List<SVM010604BO> gridList;

    /**
     * 导入列表
     */
    private List<SVM010604BO> importList;

    /**
     * 其他属性
     */
    private Object otherProperty;
}
