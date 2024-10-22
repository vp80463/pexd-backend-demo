package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServicePackageCategoryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long servicePackageCategoryId;

    private Long servicePackageId;

    private Long productCategoryId;

    private String fromDate;

    private String toDate;

    public static ServicePackageCategoryVO create() {
        ServicePackageCategoryVO entity = new ServicePackageCategoryVO();
        entity.setServicePackageCategoryId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
