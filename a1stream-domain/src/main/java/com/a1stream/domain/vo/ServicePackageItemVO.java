package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServicePackageItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long servicePackageItemId;

    private Long servicePackageId;

    private Long productId;

    private String productClassification;

    private BigDecimal qty = BigDecimal.ZERO;

    public static ServicePackageItemVO create() {
        ServicePackageItemVO entity = new ServicePackageItemVO();
        entity.setServicePackageItemId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
