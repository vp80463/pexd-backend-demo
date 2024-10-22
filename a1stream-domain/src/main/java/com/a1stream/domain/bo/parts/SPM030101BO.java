package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;
import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM030101BO extends BaseBO{

    private static final long serialVersionUID = 1L;

    private String caseNo;

    private String shipmentNo;

    private String invoiceNo;

    private BigDecimal lines;

    private BigDecimal receiptQty;

    private BigDecimal receiptAmount;

    private String importDate;

    private BigDecimal errorLines;

    private Long locationId;

    private String locationCd;

    private Long productId;

    private List<SPM030102BO> tableDataList;

    private String point;

    private Long pointId;

    private String supplier;

    private String firstCaseNo;

    private Long receiptManifestId;

    private String siteId;

    private BigDecimal qty;

    private String locationType;
}
