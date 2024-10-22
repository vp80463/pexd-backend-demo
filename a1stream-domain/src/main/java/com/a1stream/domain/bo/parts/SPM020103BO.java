package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;
import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM020103BO extends BaseBO {

    private static final long serialVersionUID = 1L;
    private Long orderItemId;
    private String partsNo;
    private String partsNm;
    private Long partsId;
    private Long supersedingPartsId;
    private String supersedingPartsCd;
    private BigDecimal onHandQty;
    private BigDecimal orderQty;
    private BigDecimal ymvnStockQty;
    private BigDecimal stdPrice;
    private BigDecimal sellingPrice;
    private BigDecimal orderAmt;
    private BigDecimal discountAmtVAT;
    private BigDecimal discount;
    private BigDecimal specialPriceVAT;
    private BigDecimal allocatedQty;
    private BigDecimal pickingQty;
    private BigDecimal shippedQty;
    private BigDecimal cancelledQty;
    private BigDecimal boQty;
    private BigDecimal taxRate;
    private String locationCd;
    private Long locationId;
    private String largeGroupNm;
    private Long batteryID;
    private String batteryFlag;
    private String boCancelSign;
    private BigDecimal salesLotQty;

    transient List<String> error;
    transient List<Object[]> errorParam;
    transient List<String> warning;
    transient List<Object[]> warningParam;

    private String beforeBoCancelSign;

    private BigDecimal sellingPriceNotVat;

    private BigDecimal actualAmtNotVat;
}