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
@Table(name="cmm_registration_document")
@Setter
@Getter
public class CmmRegistrationDocument extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="registration_document_id", unique=true, nullable=false)
    private Long registrationDocumentId;

    @Column(name="registration_date", length=8)
    private String registrationDate;

    @Column(name="registration_time", length=8)
    private String registrationTime;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="consumer_id")
    private Long consumerId;

    @Column(name="sales_order_id")
    private Long salesOrderId;

    @Column(name="service_order_id")
    private Long serviceOrderId;

    @Column(name="serialized_product_id")
    private Long serializedProductId;

    @Column(name="use_type", length=20)
    private String useType;

    @Column(name="owner_type", length=20)
    private String ownerType;

    @Column(name="purchase_type", length=40)
    private String purchaseType;

    @Column(name="psv_brand_nm", length=200)
    private String psvBrandNm;

    @Column(name="p_bike_nm", length=200)
    private String pBikeNm;

    @Column(name="mt_at_id", length=40)
    private String mtAtId;

    @Column(name="family_num")
    private Integer familyNum;

    @Column(name="num_bike")
    private Integer numBike;

    @Column(name="battery_id")
    private Long batteryId;

}
