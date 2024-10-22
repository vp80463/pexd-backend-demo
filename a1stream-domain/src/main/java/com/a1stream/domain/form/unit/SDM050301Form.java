package com.a1stream.domain.form.unit;

import java.util.List;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050301Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private boolean pageFlg = true;

    private Long promotionListId;

    private String periodDateFrom;

    private String periodDateTo;

    private String salesShipmentDateFrom;

    private String salesShipmentDateTo;

    private List<String> salesMethod;

    private Long modelId;

    private String invoiceNo;

    private String duNo;

    private String frameNo;

    private Long pointId;

    private String status;

    private String customer;

    private String pointDesc;

    private String promotion;
}
