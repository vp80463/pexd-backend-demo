package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ProductCostVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long productCostId;

    private Long productId;

    private String productCd;

    private String productNm;

    private BigDecimal cost = BigDecimal.ZERO;

    private String costType;

    private String productClassification;

    public static ProductCostVO create() {
        ProductCostVO entity = new ProductCostVO();
        entity.setProductCostId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
