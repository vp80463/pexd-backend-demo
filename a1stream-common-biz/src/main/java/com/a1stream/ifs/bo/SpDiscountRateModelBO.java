package com.a1stream.ifs.bo;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class SpDiscountRateModelBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**ifs
     * 经销商
     */
    private String dealerCode;

    /**
     * 折扣率
     */
    private BigDecimal discountRate;
}