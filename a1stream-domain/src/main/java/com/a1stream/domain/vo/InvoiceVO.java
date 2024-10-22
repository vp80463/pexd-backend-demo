package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvoiceVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long invoiceId;

    private String invoiceDate;

    private String invoiceDatetime;

    private BigDecimal taxRate = BigDecimal.ZERO;

    private String invoiceNo;

    private String orderToType;

    private Long fromFacilityId;

    private Long toFacilityId;

    private Long fromOrganizationId;

    private Long toOrganizationId;

    private Long consumerId;

    private String consumerVipNo;

    private String consumerNmFirst;

    private String consumerNmMiddle;

    private String consumerNmLast;

    private String consumerNmFull;

    private String email;

    private String mobilePhone;

    private BigDecimal invoiceAmt = BigDecimal.ZERO;

    private BigDecimal invoiceActualAmt = BigDecimal.ZERO;

    private String invoiceType;

    private Long relatedInvoiceId;

    private String relatedInvoiceNo;

    private String salesReturnReasonType;

    private String comment;

    private String productClassification;

    private String deliveryAddress;

    private String customerNm;

    private String serialNo;

    private String taxCode;

    private String vatNo;

    private Long cmmCashierId;

    private String cashierCd;

    private String cashierNm;

    private BigDecimal spAmt = BigDecimal.ZERO;

    private BigDecimal svPartsAmt = BigDecimal.ZERO;

    private BigDecimal svJobAmt = BigDecimal.ZERO;

    private String vatMobilePhone;

    private String customerCd;

    private BigDecimal invoiceActualAmtNotVat = BigDecimal.ZERO;

//    private BigDecimal amtNotVat = BigDecimal.ZERO;

    private String orderSourceType;

    public static InvoiceVO create() {
        InvoiceVO entity = new InvoiceVO();
        entity.setInvoiceId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
