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
@Table(name="invoice")
@Setter
@Getter
public class Invoice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="invoice_id", unique=true, nullable=false)
    private Long invoiceId;

    @Column(name="invoice_date", length=8)
    private String invoiceDate;

    @Column(name="invoice_datetime", length=20)
    private String invoiceDatetime;

    @Column(name="tax_rate", precision=5)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name="invoice_no", length=40)
    private String invoiceNo;

    @Column(name="order_to_type", length=40)
    private String orderToType;

    @Column(name="from_facility_id")
    private Long fromFacilityId;

    @Column(name="to_facility_id")
    private Long toFacilityId;

    @Column(name="from_organization_id")
    private Long fromOrganizationId;

    @Column(name="to_organization_id")
    private Long toOrganizationId;

    @Column(name="consumer_id")
    private Long consumerId;

    @Column(name="consumer_vip_no", length=20)
    private String consumerVipNo;

    @Column(name="consumer_nm_first", length=60)
    private String consumerNmFirst;

    @Column(name="consumer_nm_middle", length=60)
    private String consumerNmMiddle;

    @Column(name="consumer_nm_last", length=60)
    private String consumerNmLast;

    @Column(name="consumer_nm_full", length=180)
    private String consumerNmFull;

    @Column(name="email", length=100)
    private String email;

    @Column(name="mobile_phone", length=20)
    private String mobilePhone;

    @Column(name="invoice_amt", precision=18)
    private BigDecimal invoiceAmt = BigDecimal.ZERO;

    @Column(name="invoice_actual_amt", precision=18)
    private BigDecimal invoiceActualAmt = BigDecimal.ZERO;

    @Column(name="invoice_type", length=40)
    private String invoiceType;

    @Column(name="related_invoice_id")
    private Long relatedInvoiceId;

    @Column(name="related_invoice_no", length=40)
    private String relatedInvoiceNo;

    @Column(name="sales_return_reason_type", length=40)
    private String salesReturnReasonType;

    @Column(name="comment", length=256)
    private String comment;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="delivery_address", length=256)
    private String deliveryAddress;

    @Column(name="customer_nm", length=256)
    private String customerNm;

    @Column(name="serial_no", length=40)
    private String serialNo;

    @Column(name="tax_code", length=100)
    private String taxCode;

    @Column(name="vat_no", length=100)
    private String vatNo;

    @Column(name="cmm_cashier_id")
    private Long cmmCashierId;

    @Column(name="cashier_cd", length=40)
    private String cashierCd;

    @Column(name="cashier_nm", length=180)
    private String cashierNm;

    @Column(name="sp_amt", precision=18)
    private BigDecimal spAmt = BigDecimal.ZERO;

    @Column(name="sv_parts_amt", precision=18)
    private BigDecimal svPartsAmt = BigDecimal.ZERO;

    @Column(name="sv_job_amt", precision=18)
    private BigDecimal svJobAmt = BigDecimal.ZERO;

    @Column(name="vat_mobile_phone", length=20)
    private String vatMobilePhone;

    @Column(name="customer_cd", length=40)
    private String customerCd;

    @Column(name="invoice_actual_amt_not_vat", precision=18, scale=2)
    private BigDecimal invoiceActualAmtNotVat = BigDecimal.ZERO;

    @Column(name="order_source_type", length=40)
    private String orderSourceType;
}
