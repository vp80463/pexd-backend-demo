package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ReturnRequestListVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long returnRequestListId;

    private Long facilityId;

    private String requestStatus;

    private String recommendDate;

    private String requestDate;

    private String approvedDate;

    private String expireDate;

    private String productClassification;

    public static ReturnRequestListVO create() {
        ReturnRequestListVO entity = new ReturnRequestListVO();
        entity.setReturnRequestListId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
