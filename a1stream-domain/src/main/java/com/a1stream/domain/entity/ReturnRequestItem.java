package com.a1stream.domain.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.GenericGenerator;

import com.a1stream.common.model.BaseEntity;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="return_request_item")
@Setter
@Getter
public class ReturnRequestItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="return_request_item_id", unique=true, nullable=false)
    private Long returnRequestItemId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="return_request_list_id", nullable=false)
    private Long returnRequestListId;

    @Column(name="product_id", nullable=false)
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=200)
    private String productNm;

    @Column(name="request_type", length=40)
    private String requestType;

    @Column(name="request_status", length=40)
    private String requestStatus;

    @Column(name="yamaha_invoice_seq_no", length=20)
    private String yamahaInvoiceSeqNo;

    @Column(name="yamaha_external_invoice_no", length=256)
    private String yamahaExternalInvoiceNo;

    @Column(name="recommend_qty", precision=18)
    private BigDecimal recommendQty = BigDecimal.ZERO;

    @Column(name="request_qty", precision=18)
    private BigDecimal requestQty = BigDecimal.ZERO;

    @Column(name="approved_qty", precision=18)
    private BigDecimal approvedQty = BigDecimal.ZERO;

    @Column(name="return_price", precision=18)
    private BigDecimal returnPrice = BigDecimal.ZERO;

    @Column(name="sales_order_item_id")
    private Long salesOrderItemId;

    @Column(name="sales_order_id")
    private Long salesOrderId;

    @Column(name="product_classification", length=40)
    private String productClassification;


}
