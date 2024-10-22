package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class LocationVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long locationId;

    private Long facilityId;

    private Long workzoneId;

    private String locationCd;

    private String locationType;

    private Long binTypeId;

    private String primaryFlag;

    public static LocationVO create() {
        LocationVO entity = new LocationVO();
        entity.setLocationId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
