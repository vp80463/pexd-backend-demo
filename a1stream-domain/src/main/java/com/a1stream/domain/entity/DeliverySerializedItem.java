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
@Table(name="delivery_serialized_item")
@Setter
@Getter
public class DeliverySerializedItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="delivery_serialized_item_id", unique=true, nullable=false)
    private Long deliverySerializedItemId;

    @Column(name="serialized_product_id", nullable=false)
    private Long serializedProductId;

    @Column(name="order_serialized_item_id")
    private Long orderSerializedItemId;

    @Column(name="delivery_order_item_id", nullable=false)
    private Long deliveryOrderItemId;

    @Column(name="delivery_order_id", nullable=false)
    private Long deliveryOrderId;

    @Column(name="out_cost", precision=18, scale=4)
    private BigDecimal outCost = BigDecimal.ZERO;


}
