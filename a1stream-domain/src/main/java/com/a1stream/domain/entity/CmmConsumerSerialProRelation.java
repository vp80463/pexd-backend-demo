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
@Table(name="cmm_consumer_serial_pro_relation")
@Setter
@Getter
public class CmmConsumerSerialProRelation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="consumer_serialized_product_relation_id", unique=true, nullable=false)
    private Long consumerSerializedProductRelationId;

    @Column(name="serialized_product_id", nullable=false)
    private Long serializedProductId;

    @Column(name="consumer_id", nullable=false)
    private Long consumerId;

    @Column(name="consumer_serialized_product_relation_type_id", length=40)
    private String consumerSerializedProductRelationTypeId;

    @Column(name="owner_flag", length=1)
    private String ownerFlag;

    @Column(name="from_date", length=8)
    private String fromDate;

    @Column(name="to_date", length=8)
    private String toDate;
}
