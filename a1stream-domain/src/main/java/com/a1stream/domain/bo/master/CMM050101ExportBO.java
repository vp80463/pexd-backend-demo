package com.a1stream.domain.bo.master;

import java.io.Serial;
import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;
import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liu chaoran
 */
@Getter
@Setter
public class CMM050101ExportBO extends BaseBO {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty(index = 0)
    private String brandCd;

    private String partsCd;

    private String partsNm;

    private String largeGroupNm;

    private String middleGroupNm;

    private String registrationDate;

    private BigDecimal priceExcludeVAT;

    private BigDecimal priceIncludeVAT;

    private BigDecimal stdPurchasePrice;
}