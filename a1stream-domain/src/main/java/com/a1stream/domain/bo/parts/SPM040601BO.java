package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;
import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM040601BO extends BaseBO{

    private static final long serialVersionUID = 1L;

    private String partsNo;

    private String partsNm;

    private Long partsId;

    private BigDecimal price;

    private BigDecimal minPurchaseQty;

    private BigDecimal purchaseLot;

    private BigDecimal orderQty;

    private String cancelFlag;

    private BigDecimal stdRetailPrice;

    private String errorMessage;

    private String warningMessage;
    
    transient List<String> error;

    transient List<Object[]> errorParam;

    transient List<String> warning;

    transient List<Object[]> warningParam;

}
