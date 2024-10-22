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
@Table(name="service_pdi")
@Setter
@Getter
public class ServicePdi extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_pdi_id", unique=true, nullable=false)
    private Long servicePdiId;

    @Column(name="serialized_product_id")
    private Long serializedProductId;

    @Column(name="consumer_id")
    private Long consumerId;

    @Column(name="pdi_pic", length=200)
    private String pdiPic;

    @Column(name="pdi_date", length=8)
    private String pdiDate;

    @Column(name="start_time", length=4)
    private String startTime;

    @Column(name="finish_time", length=4)
    private String finishTime;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="pdi_pic_id")
    private Long pdiPicId;
}
