package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class PurchaseOrderVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long purchaseOrderId;

    private String orderNo;

    private String productClassification;

    private Long facilityId;

    private Long cmmFacilityId;

    private String facilityNm;

    private Long supplierId;

    private String supplierCd;

    private String supplierNm;

    private String orderDate;

    private Long orderPicId;

    private String orderPicNm;

    private String approveDate;

    private Long approvePicId;

    private String approvePicNm;

    private String orderStatus;

    private String boCancelFlag;

    private String orderPriorityType;

    private String orderMethodType;

    private String deliverPlanDate;

    private String salesOrderNo;

    private String supplierOrderNo;

    private BigDecimal totalQty = BigDecimal.ZERO;

    private BigDecimal totalAmount = BigDecimal.ZERO;

    private BigDecimal totalActualQty = BigDecimal.ZERO;

    private BigDecimal totalActualAmt = BigDecimal.ZERO;

    private String consigneeId;

    private String memo;

    private String facilityCd;

    private String supplierConfirmDate;

    private String supplierWarehouseCd;

    public static PurchaseOrderVO create() {
        PurchaseOrderVO entity = new PurchaseOrderVO();
        entity.setPurchaseOrderId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }

}
