package com.a1stream.domain.vo;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;



@Setter
@Getter
public class InvoiceItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long invoiceItemId;

    private Long invoiceId;

    private Integer seqNo = CommonConstants.INTEGER_ZERO;

    private Long productId;

    private String productCd;

    private String productNm;

    private BigDecimal qty = BigDecimal.ZERO;

    private BigDecimal sellingPriceNotVat = BigDecimal.ZERO;

    private BigDecimal amt = BigDecimal.ZERO;

    private BigDecimal cost = BigDecimal.ZERO;

    private BigDecimal standardPrice = BigDecimal.ZERO;

    private BigDecimal discountOffRate = BigDecimal.ZERO;

    private String caseNo;

    private Long relatedSoItemId;

    private String salesOrderNo;

    private String orderDate;

    private String customerOrderNo;

    private Long orderedProductId;

    private String orderedProductCd;

    private String orderedProductNm;

    private Long locationId;

    private String locationCd;

    private Long relatedInvoiceItemId;

    private String orderType;

    private String orderSourceType;

    private String productClassification;

    private BigDecimal taxRate = BigDecimal.ZERO;

    private BigDecimal sellingPrice = BigDecimal.ZERO;

    private BigDecimal amtNotVat = BigDecimal.ZERO;

    private Long salesOrderId;

    private Long relatedDoItemId;

    public static InvoiceItemVO create() {
        InvoiceItemVO entity = new InvoiceItemVO();
        entity.setInvoiceItemId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }

}
