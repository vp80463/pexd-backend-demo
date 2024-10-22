package com.a1stream.common.bo;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesReturnBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String invoiceNo;

    private Long invoiceId;

    private Long customerId;//to_org_id

    private String customerCd;

    private String customerName;

    private Long fromOrganizationId;

    private Long consumerId;

    private Long pointId;

    private String reason;

    private String partNo;

    private String invoiceDate;

    private String orderToType;

    private String deliveryOrderItemId;

    private List<SalesReturnDetailBO> details;
}