package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class RemindScheduleRecordVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long remindScheduleRecordId;

    private Long facilityId;

    private String recordDate;

    private String recordSubject;

    private String description;

    private Long remindScheduleId;

    private String satisfactionPoint;
}
