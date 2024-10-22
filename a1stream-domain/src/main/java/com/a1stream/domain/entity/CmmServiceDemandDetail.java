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
@Table(name="cmm_service_demand_detail")
@Setter
@Getter
public class CmmServiceDemandDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_demand_detail_id", unique=true, nullable=false)
    private Long serviceDemandDetailId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="serialized_product_id")
    private Long serializedProductId;

    @Column(name="service_demand_id")
    private Long serviceDemandId;

    @Column(name="fsc_result_flag", length=1)
    private String fscResultFlag;

    @Column(name="fsc_usage", length=40)
    private String fscUsage;

    @Column(name="fsc_result_date", length=8)
    private String fscResultDate;

    @Column(name="comment", length=256)
    private String comment;


}
