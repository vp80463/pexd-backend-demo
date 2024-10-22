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
@Table(name="service_order_battery")
@Setter
@Getter
public class ServiceOrderBattery extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_order_battery_id", unique=true, nullable=false)
    private Long serviceOrderBatteryId;

    @Column(name="service_order_id")
    private Long serviceOrderId;

    @Column(name="battery_type", length=40)
    private String batteryType;

    @Column(name="cmm_serialized_product_id")
    private Long cmmSerializedProductId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=256)
    private String productNm;

    @Column(name="battery_id")
    private Long batteryId;

    @Column(name="battery_no", length=17)
    private String batteryNo;

    @Column(name="warranty_start_date", length=8)
    private String warrantyStartDate;

    @Column(name="warranty_term", length=200)
    private String warrantyTerm;

    @Column(name="new_product_id")
    private Long newProductId;

    @Column(name="new_product_cd", length=40)
    private String newProductCd;

    @Column(name="new_product_nm", length=256)
    private String newProductNm;

    @Column(name="new_battery_id")
    private Long newBatteryId;

    @Column(name="new_battery_no", length=17)
    private String newBatteryNo;

    @Column(name="selling_price", precision=18)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name="order_qty", precision=18)
    private BigDecimal orderQty = BigDecimal.ZERO;


}
