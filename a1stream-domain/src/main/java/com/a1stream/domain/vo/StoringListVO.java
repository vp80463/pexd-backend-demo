package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class StoringListVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long storingListId;

    private Long facilityId;

    private String storingListNo;

    private Long storingPicId;

    private String storingPicNm;

    private String receiptDate;

    private String instructionDate;

    private String instructionTime;

    private String completedDate;

    private String completedTime;

    private String inventoryTransactionType;

    private String productClassification;

    public static StoringListVO create() {
        StoringListVO entity = new StoringListVO();
        entity.setStoringListId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
