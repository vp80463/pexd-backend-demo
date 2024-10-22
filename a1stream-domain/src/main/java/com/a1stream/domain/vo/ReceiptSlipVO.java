package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ReceiptSlipVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long receiptSlipId;

    private String slipNo;

    private String receivedDate;

    private BigDecimal receiptSlipTotalAmt = BigDecimal.ZERO;

    private Long receivedFacilityId;

    private Long receivedOrganizationId;

    private Long receivedPicId;

    private String receivedPicNm;

    private String supplierDeliveryDate;

    private Long fromOrganizationId;

    private Long fromFacilityId;

    private String receiptSlipStatus;

    private String inventoryTransactionType;

    private String commercialInvoiceNo;

    private String returnReasonContent;

    private String storingPicNm;

    private String storingPicId;

    private String storingStartDate;

    private String storingStartTime;

    private String storingEndDate;

    private String storingEndTime;

    private String productClassification;

    public static ReceiptSlipVO create() {
        ReceiptSlipVO entity = new ReceiptSlipVO();
        entity.setReceiptSlipId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }

}
