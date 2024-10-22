package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ProductStockStatusVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long productStockStatusId;

    private Long facilityId;

    private Long productId;

    private String productStockStatusType;

    private BigDecimal quantity = BigDecimal.ZERO;

    private String productClassification;

    public static ProductStockStatusVO create() {
        ProductStockStatusVO entity = new ProductStockStatusVO();
        entity.setProductStockStatusId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
