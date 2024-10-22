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
@Table(name="receipt_slip")
@Setter
@Getter
public class ReceiptSlip extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="receipt_slip_id", unique=true, nullable=false)
    private Long receiptSlipId;

    @Column(name="slip_no", length=40)
    private String slipNo;

    @Column(name="received_date", length=8)
    private String receivedDate;

    @Column(name="receipt_slip_total_amt", precision=18)
    private BigDecimal receiptSlipTotalAmt = BigDecimal.ZERO;

    @Column(name="received_facility_id")
    private Long receivedFacilityId;

    @Column(name="received_organization_id")
    private Long receivedOrganizationId;

    @Column(name="received_pic_id")
    private Long receivedPicId;

    @Column(name="received_pic_nm", length=60)
    private String receivedPicNm;

    @Column(name="supplier_delivery_date", length=8)
    private String supplierDeliveryDate;

    @Column(name="from_organization_id")
    private Long fromOrganizationId;

    @Column(name="from_facility_id")
    private Long fromFacilityId;

    @Column(name="receipt_slip_status", length=40)
    private String receiptSlipStatus;

    @Column(name="inventory_transaction_type", length=40)
    private String inventoryTransactionType;

    @Column(name="commercial_invoice_no", length=40)
    private String commercialInvoiceNo;

    @Column(name="return_reason_content")
    private String returnReasonContent;

    @Column(name="storing_pic_nm", length=60)
    private String storingPicNm;

    @Column(name="storing_pic_id", length=40)
    private String storingPicId;

    @Column(name="storing_start_date", length=8)
    private String storingStartDate;

    @Column(name="storing_start_time", length=8)
    private String storingStartTime;

    @Column(name="storing_end_date", length=8)
    private String storingEndDate;

    @Column(name="storing_end_time", length=8)
    private String storingEndTime;

    @Column(name="product_classification", length=40)
    private String productClassification;


}
