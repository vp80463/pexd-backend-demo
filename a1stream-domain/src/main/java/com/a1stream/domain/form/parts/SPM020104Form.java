package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.SPM020104BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM020104Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long deliveryOrderId;

    private Long salesOrderId;

    private String orderNo;

    private String orderType;

    private String customer;

    private Long customerId;

    private String deliveryPlanDate;

    private String memo;

    private List<SPM020104BO> allTableDataList;

    private Long pointId;
}
