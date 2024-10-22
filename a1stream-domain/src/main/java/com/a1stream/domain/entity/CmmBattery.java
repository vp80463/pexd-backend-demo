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
@Table(name="cmm_battery")
@Setter
@Getter
public class CmmBattery extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="battery_id", unique=true, nullable=false)
    private Long batteryId;

    @Column(name="battery_no", length=17)
    private String batteryNo;

    @Column(name="battery_cd", length=20)
    private String batteryCd;

    @Column(name="battery_status", length=20)
    private String batteryStatus;

    @Column(name="selling_price", precision=18)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name="sale_date", length=8)
    private String saleDate;

    @Column(name="original_flag", length=1)
    private String originalFlag;

    @Column(name="service_calculate_date", length=8)
    private String serviceCalculateDate;

    @Column(name="serialized_product_id")
    private Long serializedProductId;

    @Column(name="from_date", length=8)
    private String fromDate;

    @Column(name="to_date", length=8)
    private String toDate;

    @Column(name="position_sign", length=8)
    private String positionSign;

    @Column(name="product_id", nullable=false)
    private Long productId;
}
