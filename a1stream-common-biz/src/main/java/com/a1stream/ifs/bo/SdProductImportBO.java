package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SdProductImportBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String modelType;
    private String modelTypeName;
    private String modelCode;
    private String modelName;
    private String effectiveDate;
    private String outEffectiveDate;
    private String productCode;
    private String productSalesName;
    private String colorCode;
    private String colorName;
    private String registeredModel;
    private String modelYear;
    private Integer displacement;
    private String sstSign;
    private String localModelCode;
    private BigDecimal stdRetailPrice;

}