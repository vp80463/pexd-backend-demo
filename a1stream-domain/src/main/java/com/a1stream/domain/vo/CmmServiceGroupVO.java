package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmServiceGroupVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceGroupId;

    private String serviceGroupCd;

    private String description;

    public static CmmServiceGroupVO create() {
        CmmServiceGroupVO entity = new CmmServiceGroupVO();
        entity.setServiceGroupId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
