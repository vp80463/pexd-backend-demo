package com.a1stream.domain.vo;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CalendarVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long calendarInfoId;

    private Long facilityId;

    private String targetYearMonth;

    private Integer daySeqId = CommonConstants.INTEGER_ZERO;

    private String workingDayFlag;


}
