package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class BatteryVO extends BaseVO {

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

    private Long cmmBatteryInfoId;

    private Long productId;

    public static BatteryVO create() {
        
        BatteryVO entity = new BatteryVO();
        entity.setBatteryId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }

    public static BatteryVO copyFromCmm(CmmBatteryVO cmmData, String siteId, Long locSerializedProductId) {

        BatteryVO entity = BeanMapUtils.mapTo(cmmData, BatteryVO.class);

        entity.setBatteryId(IdUtils.getSnowflakeIdWorker().nextId());
        entity.setSiteId(siteId);
        if (locSerializedProductId != null) {
        	entity.setSerializedProductId(locSerializedProductId);
        }
        entity.setCmmBatteryInfoId(cmmData.getBatteryId());

        return entity;
    }
}
