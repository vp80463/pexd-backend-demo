package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServiceOrderBatteryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceOrderBatteryId;

    private Long serviceOrderId;

    private String batteryType;

    private Long cmmSerializedProductId;

    private Long productId;

    private String productCd;

    private String productNm;

    private Long batteryId;

    private String batteryNo;

    private String warrantyStartDate;

    private String warrantyTerm;

    private Long newProductId;

    private String newProductCd;

    private String newProductNm;

    private Long newBatteryId;

    private String newBatteryNo;

    private BigDecimal sellingPrice = BigDecimal.ZERO;

    private BigDecimal orderQty = BigDecimal.ZERO;

    public static ServiceOrderBatteryVO create(String siteId, Long serviceOrderId) {
        ServiceOrderBatteryVO entity = new ServiceOrderBatteryVO();

        entity.setSiteId(siteId);
        entity.setServiceOrderId(serviceOrderId);

        return entity;
    }
}
