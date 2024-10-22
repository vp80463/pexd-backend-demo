package com.a1stream.domain.vo;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class QueueOrderBkListVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long dbId;

    private Long orderId;

    private String orderType;

    private String orderDate;

    private Integer sendTimes = CommonConstants.INTEGER_ZERO;

    private String status;

    private String statusMessage;
}
