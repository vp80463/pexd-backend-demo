package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmSerializedProductFscRemindVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serializedProductId;

    private Long facilityId;

    private Long orderId;

    private Long nextServiceDemandId;

    private String nextRemindGenDate;
}
