package com.a1stream.domain.entity;

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
@Table(name="receipt_manifest")
@Setter
@Getter
public class ReceiptManifest extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="receipt_manifest_id", unique=true, nullable=false)
    private Long receiptManifestId;

    @Column(name="import_date", length=8)
    private String importDate;

    @Column(name="import_time", length=8)
    private String importTime;

    @Column(name="supplier_invoice_no", length=40)
    private String supplierInvoiceNo;

    @Column(name="supplier_shipment_no", length=40)
    private String supplierShipmentNo;

    @Column(name="supplier_shipped_date", length=8)
    private String supplierShippedDate;

    @Column(name="from_organization")
    private Long fromOrganization;

    @Column(name="to_organization")
    private Long toOrganization;

    @Column(name="from_facility_id")
    private Long fromFacilityId;

    @Column(name="to_facility_id")
    private Long toFacilityId;

    @Column(name="manifest_status", length=40)
    private String manifestStatus;

    @Column(name="product_classification", length=40)
    private String productClassification;


}
