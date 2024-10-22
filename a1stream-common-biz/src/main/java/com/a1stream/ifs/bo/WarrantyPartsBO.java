package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarrantyPartsBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String modelCode;
    private String type;
    private BigDecimal valueData;
    private List<WarrantyPartsItemBO> resultList;
}