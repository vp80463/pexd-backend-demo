package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM040401Form extends BaseForm{

    private static final long serialVersionUID = 1L;

    private String dateFrom;

    private String dateTo;

    private String poNo;

    private List<String> status;

    private List<String> type;

    private List<String> method;

    private String salesOrderNo;

    private Long orderPicId;

    private Long pointId;

    private String supplierOrderNo;

    private Long supplierId;

    private Long purchaseOrderId;

    private Integer updateCount;

    private String orderNo;
}
