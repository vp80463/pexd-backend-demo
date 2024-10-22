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
public class SDCommonCheckBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8238406834590719623L;

    /**
     * 站点
     */
    private String siteId;

    /**
     * 车架号
     */
    private String frameNo;

    /**
     * 仓库代码
     */
    private String pointCd;

    /**
     * 人员代码
     */
    private String picCd;

    /**
     * 产品代码
     */
    private String modelCd;

    /**
     * 仓库id
     */
    private Long pointId;

    /**
     * 产品id
     */
    private Long modelId;

    /**
     * 电池号1
     */
    private String batteryNo1;

    /**
     * 电池号2
     */
    private String batteryNo2;

    /**
     * 电池id1
     */
    private Long batteryId1;

    /**
     * 电池id2
     */
    private Long batteryId2;

    /**
     * 销售日期
     */
    private String salesDate;
}