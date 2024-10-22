package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpQuotationModelBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String warehouseCode;
    private String dealerCode;
    private String consignee;
    private String salesOrderNo;
    private String boCancelSign;
    private String orderType;
    private String createdDate;
    private List<SpQuotationItemModelBO> orderItemList = new ArrayList<>();
}