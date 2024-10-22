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
@Table(name="service_request_battery")
@Setter
@Getter
public class ServiceRequestBattery extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_request_battery_id", unique=true, nullable=false)
    private Long serviceRequestBatteryId;

    @Column(name="service_request_id")
    private Long serviceRequestId;

    @Column(name="service_order_battery_id")
    private Long serviceOrderBatteryId;

    @Column(name="battery_product_id")
    private Long batteryProductId;

    @Column(name="battery_cd", length=20)
    private String batteryCd;

    @Column(name="battery_no", length=17)
    private String batteryNo;

    @Column(name="battery_id")
    private Long batteryId;

    @Column(name="used_qty", precision=18)
    private BigDecimal usedQty = BigDecimal.ZERO;

    @Column(name="payment_amt", precision=18)
    private BigDecimal paymentAmt = BigDecimal.ZERO;

    @Column(name="select_flag", length=1)
    private String selectFlag;


}
