package com.a1stream.common.bo;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyPurchaseOrderBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long purchaseOrderId;

    private String planDate;

    private String comment;

    private List<ModifyPurchaseOrderItemBO> purchaseOrderItems;
}