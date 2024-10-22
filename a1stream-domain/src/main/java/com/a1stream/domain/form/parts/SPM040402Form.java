package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.SPM040402BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM040402Form extends BaseForm{

    private static final long serialVersionUID = 1L;

    private Long purchaseOrderId;

    private List<SPM040402BO> gridDataList;

    private Long productId;

    private String deliveryPlanDate;

    private Integer updateCount;

    private String orderNo;

}
