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
@Table(name="invoice_serialized_item")
@Setter
@Getter
public class InvoiceSerializedItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="invoice_serialized_item_id", unique=true, nullable=false)
    private Long invoiceSerializedItemId;

    @Column(name="invoice_id")
    private Long invoiceId;

    @Column(name="invoice_item_id")
    private Long invoiceItemId;

    @Column(name="serialized_product_id")
    private Long serializedProductId;

}
