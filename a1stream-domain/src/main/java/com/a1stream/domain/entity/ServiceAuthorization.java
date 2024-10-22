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
@Table(name="service_authorization")
@Setter
@Getter
public class ServiceAuthorization extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_authorization_id", unique=true, nullable=false)
    private Long serviceAuthorizationId;

    @Column(name="authorization_no", length=40)
    private String authorizationNo;

    @Column(name="serialized_item_no", length=40)
    private String serializedItemNo;

    @Column(name="point_id")
    private Long pointId;

    @Column(name="serialized_product_id")
    private Long serializedProductId;

    @Column(name="from_date", length=8)
    private String fromDate;

    @Column(name="to_date", length=8)
    private String toDate;


}
