package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;
import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM040401BO extends BaseBO{

    private static final long serialVersionUID = 1L;

    private String poNo;

    private Long purchaseOrderId;

    private String supplierOrderNo;

    private String orderDate;

    private String confirmDate;

    private String orderStatus;

    private String orderType;

    private String method;

    private String supplierCd;

    private String supplierNm;

    private String pointCd;

    private String pointNm;

    private String deliveryPlanDate;

    private BigDecimal orderLines;

    private BigDecimal amt;

    private String orderPic;

    private String memo;

    private String supplier;

    private String point;

    private Boolean deleteFlag;

    private List<SPM040402BO> tableDataList;

    private Boolean deleteDisabled;

    private Boolean otherButtonDisabled;

}
