package com.a1stream.common.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportForm {

    private Long deliveryOrderId;

    private Long returnInvoiceId;

    private List<Long> receiptSlipIds;
}
