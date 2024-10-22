package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServiceOrderItemOtherBrandVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceOrderItemOtherBrandId;

    private Long serviceOrderId;

    private String settleType;

    private String serviceCategory;

    private Long serviceDemand;

    private String itemContent;

    private Long itemId;

    private String itemCd;

    private String productClassification;

    private BigDecimal stdManhour = BigDecimal.ZERO;

    private BigDecimal standardPrice = BigDecimal.ZERO;

    private BigDecimal sellingPrice = BigDecimal.ZERO;

    private BigDecimal orderQty = BigDecimal.ZERO;

    private BigDecimal discount = BigDecimal.ZERO;

    private BigDecimal specialPrice = BigDecimal.ZERO;

    private BigDecimal taxRate = BigDecimal.ZERO;

    private BigDecimal sellingPriceNotVat = BigDecimal.ZERO;

    private BigDecimal actualAmt = BigDecimal.ZERO;

    private BigDecimal actualAmtNotVat = BigDecimal.ZERO;

    public static ServiceOrderItemOtherBrandVO create(String siteId, Long serviceOrderId) {
        ServiceOrderItemOtherBrandVO entity = new ServiceOrderItemOtherBrandVO();

        entity.setSiteId(siteId);
        entity.setServiceOrderId(serviceOrderId);

        return entity;
    }
}
