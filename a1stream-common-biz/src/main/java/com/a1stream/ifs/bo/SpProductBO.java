package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpProductBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String partNo;
    private String partNameEng;
    private String partNameNative;
    private String supersedingPart;
    private BigDecimal retailStandardPrice;
    private BigDecimal wholesaleStandardPrice;
    private String productCategory;
    private String partSizeH;
    private String partSizeW;
    private String partSizeL;
    private String partWeight;
    private String minSalesLot;
    private String nonsalesId;
    private String retailPriceRevisionDate;
    private String wholesalePriceRevisionDate;
    private String supersedingRevisionDate;
    private String registerDate;
    private String createDate;
    private String serviceCategoryCode;
    private String serviceCategoryName;
    private String optionCode1;
    private String optionName1;
    private List<String> lots;
}