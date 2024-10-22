package com.a1stream.domain.vo;

import java.time.LocalDateTime;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServiceRequestEditHistoryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceRequestEditHistoryId;

    private Long serviceRequestId;

    private String requestStatus;

    private LocalDateTime changeDate;

    private Long reportPicId;

    private String reportPicCd;

    private String reportPicNm;

    private String comment;
}
