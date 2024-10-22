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
@Table(name="receipt_manifest_serialized_item")
@Setter
@Getter
public class ReceiptManifestSerializedItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="receipt_manifest_serialized_item_id", unique=true, nullable=false)
    private Long receiptManifestSerializedItemId;

    @Column(name="receipt_manifest_item_id")
    private Long receiptManifestItemId;

    @Column(name="serialized_product_id")
    private Long serializedProductId;

    @Column(name="complete_flag", length=1)
    private String completeFlag;

    @Column(name="receipt_slip_id")
    private Long receiptSlipId;


}
