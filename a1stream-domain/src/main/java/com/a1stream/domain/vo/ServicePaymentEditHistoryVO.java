package com.a1stream.domain.vo;

import java.time.LocalDateTime;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServicePaymentEditHistoryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long servicePaymentEditHistoryId;

    private Long paymentId;

    private String paymentStatus;

    private LocalDateTime changeDate;

    private Long reportFacilityId;

    private Long reportPicId;

    private String reportPicCd;

    private String reportPicNm;


}
