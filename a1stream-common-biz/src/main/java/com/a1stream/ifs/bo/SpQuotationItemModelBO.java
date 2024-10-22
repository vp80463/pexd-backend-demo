package com.a1stream.ifs.bo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpQuotationItemModelBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String partsNo;
    private String orderQty;
    private String price;
}