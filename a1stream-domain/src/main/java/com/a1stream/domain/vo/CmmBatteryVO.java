package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmBatteryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long batteryId;

    private String batteryNo;

    private String batteryCd;

    private String batteryStatus;

    private BigDecimal sellingPrice = BigDecimal.ZERO;

    private String saleDate;

    private String originalFlag;

    private String serviceCalculateDate;

    private Long serializedProductId;

    private String fromDate;

    private String toDate;

    private String positionSign;

    private Long productId;

    public static CmmBatteryVO create() {
        CmmBatteryVO entity = new CmmBatteryVO();
        entity.setBatteryId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
