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
@Table(name="cmm_serialized_product_fsc_remind")
@Setter
@Getter
public class CmmSerializedProductFscRemind extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="serialized_product_id", unique=true, nullable=false)
    private Long serializedProductId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="order_id")
    private Long orderId;

    @Column(name="next_service_demand_id")
    private Long nextServiceDemandId;

    @Column(name="next_remind_gen_date", length=8)
    private String nextRemindGenDate;

}
