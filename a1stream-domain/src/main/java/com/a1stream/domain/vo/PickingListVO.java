package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class PickingListVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long pickingListId;

    private Long facilityId;

    private String pickingListNo;

    private String pickingListDate;

    private String startedDate;

    private String startedTime;

    private String finishedDate;

    private String finishedTime;

    private String inventoryTransactionType;

    private String productClassification;

    public static PickingListVO create() {
        PickingListVO entity = new PickingListVO();
        entity.setPickingListId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
