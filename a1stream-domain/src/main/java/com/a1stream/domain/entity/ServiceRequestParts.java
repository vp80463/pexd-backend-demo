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
@Table(name="service_request_parts")
@Setter
@Getter
public class ServiceRequestParts extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_request_parts_id", unique=true, nullable=false)
    private Long serviceRequestPartsId;

    @Column(name="service_request_id")
    private Long serviceRequestId;

    @Column(name="service_order_parts_id")
    private Long serviceOrderPartsId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="used_qty", precision=18)
    private BigDecimal usedQty = BigDecimal.ZERO;

    @Column(name="payment_product_cd", length=40)
    private String paymentProductCd;

    @Column(name="payment_product_qty", precision=18)
    private BigDecimal paymentProductQty = BigDecimal.ZERO;

    @Column(name="payment_product_price", precision=18)
    private BigDecimal paymentProductPrice = BigDecimal.ZERO;

    @Column(name="payment_amount", precision=18)
    private BigDecimal paymentAmount = BigDecimal.ZERO;

    @Column(name="payment_product_receive_date", length=8)
    private String paymentProductReceiveDate;

    @Column(name="select_flag", length=1)
    private String selectFlag;


}
