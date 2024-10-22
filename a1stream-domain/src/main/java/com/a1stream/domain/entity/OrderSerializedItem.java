package com.a1stream.domain.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.GenericGenerator;

import com.a1stream.common.constants.CommonConstants;
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
@Table(name="order_serialized_item")
@Setter
@Getter
public class OrderSerializedItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="order_serialized_item_id", unique=true, nullable=false)
    private Long orderSerializedItemId;

    @Column(name="serialized_product_id")
    private Long serializedProductId;

    @Column(name="order_item_id")
    private Long orderItemId;

    @Column(name="service_order_id")
    private Long serviceOrderId;

    @Column(name="sales_order_id")
    private Long salesOrderId;

    @Column(name="price", precision=18)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name="mileage")
    private Integer mileage = CommonConstants.INTEGER_ZERO;


}
