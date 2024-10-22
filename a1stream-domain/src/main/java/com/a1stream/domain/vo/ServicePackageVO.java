package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServicePackageVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long servicePackageId;

    private String packageCd;

    private String localDescription;

    private String salesDescription;

    private String englishDescription;

    private String serviceCategory;

    private String fromDate;

    private String toDate;

    public static ServicePackageVO create() {
        ServicePackageVO entity = new ServicePackageVO();
        entity.setServicePackageId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
