package com.a1stream.domain.entity;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseEntity;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;


@Entity
@Table(name="invoice_item")
@Setter
@Getter
public class InvoiceItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="invoice_item_id", unique=true, nullable=false)
    private Long invoiceItemId;

    @Column(name="invoice_id")
    private Long invoiceId;

    @Column(name="seq_no")
    private Integer seqNo = CommonConstants.INTEGER_ZERO;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=200)
    private String productNm;

    @Column(name="qty", precision=18)
    private BigDecimal qty = BigDecimal.ZERO;

    @Column(name="selling_price_not_vat", precision=18)
    private BigDecimal sellingPriceNotVat = BigDecimal.ZERO;

    @Column(name="amt", precision=18)
    private BigDecimal amt = BigDecimal.ZERO;

    @Column(name="cost", precision=18, scale=4)
    private BigDecimal cost = BigDecimal.ZERO;

    @Column(name="standard_price", precision=18)
    private BigDecimal standardPrice = BigDecimal.ZERO;

    @Column(name="discount_off_rate", precision=18, scale=2)
    private BigDecimal discountOffRate = BigDecimal.ZERO;

    @Column(name="case_no", length=10)
    private String caseNo;

    @Column(name="related_so_item_id")
    private Long relatedSoItemId;

    @Column(name="sales_order_no", length=40)
    private String salesOrderNo;

    @Column(name="order_date", length=8)
    private String orderDate;

    @Column(name="customer_order_no", length=40)
    private String customerOrderNo;

    @Column(name="ordered_product_id")
    private Long orderedProductId;

    @Column(name="ordered_product_cd", length=40)
    private String orderedProductCd;

    @Column(name="ordered_product_nm", length=200)
    private String orderedProductNm;

    @Column(name="location_id")
    private Long locationId;

    @Column(name="location_cd", length=40)
    private String locationCd;

    @Column(name="related_invoice_item_id")
    private Long relatedInvoiceItemId;

    @Column(name="order_type", length=40)
    private String orderType;

    @Column(name="order_source_type", length=40)
    private String orderSourceType;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="tax_rate", precision=5)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name="selling_price", precision=18, scale=2)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name="amt_not_vat", precision=18, scale=2)
    private BigDecimal amtNotVat = BigDecimal.ZERO;

    @Column(name="sales_order_id")
    private Long salesOrderId;

    @Column(name="related_do_item_id")
    private Long relatedDoItemId;
}
