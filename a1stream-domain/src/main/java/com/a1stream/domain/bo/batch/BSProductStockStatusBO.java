package com.a1stream.domain.bo.batch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BSProductStockStatusBO {

    private Long facilityid;
    private Long productid;
    private String productcd;
    private String productnm;
    private String sumquantity;
    private String cost;
    private Long serializedproid;
}
