package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class MenuSettingForm extends BaseForm{

    @Serial
    private static final long serialVersionUID = -2030127098187980407L;

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
     * 用户id
     */
    private String userId;

    /**
     * 经销商检索字段
     */
    private String dealerRetrieve;

    /**
     * 经销商cd
     */
    private String dealerCd;
}