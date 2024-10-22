package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class RemindScheduleVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long remindScheduleId;

    private Long facilityId;

    private String activityFlag;

    private String remindedDate;

    private String remindDueDate;

    private String expireDate;

    private String remindType;

    private String productCd;

    private Long serializedProductId;

    private Long remindSettingId;

    private String remindContents;

    private String eventStartDate;

    private String eventEndDate;

    private Long relatedOrderId;

    private Long serviceDemandId;

    private String roleList;
}
