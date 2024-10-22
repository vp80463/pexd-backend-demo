package com.a1stream.common.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class JasperExportDetailTestBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 规格
     */
    private String accSpecification;

    /**
     * 库位
     */
    private String locationCd;

    /**
     * 名称
     */
    private String accCategoryNm;

    /**
     * 数量
     */
    private BigDecimal deliveryQty;

    /**
     * 产品编码
     */
    private String productCd;

    /**
     * 颜色
     */
    private String colorNm;
}