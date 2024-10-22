package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class PoSoItemRelationVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long poSoItemRelationId;

    private Long facilityId;

    private String facilityCd;

    private String facilityAbbr;

    private String facilityNm;

    private Long supplierId;

    private String supplierCd;

    private String supplierNm;

    private String poNo;

    private String soNo;

    private Long purchaseOrderId;

    private Long salesOrderId;

    private Long purchaseOrderItemId;

    private Long salesOrderItemId;

    private Long productId;

    private String productCd;

    private String productNm;

    private BigDecimal poQty = BigDecimal.ZERO;

    private BigDecimal soQty = BigDecimal.ZERO;

    private String orderSourceType;

    private String productClassification;


}
