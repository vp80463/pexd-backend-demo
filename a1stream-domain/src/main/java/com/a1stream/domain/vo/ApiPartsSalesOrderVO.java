package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ApiPartsSalesOrderVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long crmOrderId;

    private String crmOrderNo;

    private String orderStatus;

    private String dealerCd;

    private String firstNm;

    private String middleNm;

    private String lastNm;

    private String crmConsumerId;

    private String deliveryPlanDate;

    private String mobilePhone;

    private String pointId;

    private Long partyId;


}
