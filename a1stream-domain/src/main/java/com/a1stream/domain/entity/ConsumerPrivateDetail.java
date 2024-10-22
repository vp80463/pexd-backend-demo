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
@Table(name="consumer_private_detail")
@Setter
@Getter
public class ConsumerPrivateDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="consumer_private_detail_id", unique=true, nullable=false)
    private Long consumerPrivateDetailId;

    @Column(name="consumer_id")
    private Long consumerId;

    @Column(name="last_nm", length=60)
    private String lastNm;

    @Column(name="middle_nm", length=60)
    private String middleNm;

    @Column(name="first_nm", length=60)
    private String firstNm;

    @Column(name="consumer_full_nm", length=180)
    private String consumerFullNm;

    @Column(name="consumer_retrieve", length=200)
    private String consumerRetrieve;

    @Column(name="mobile_phone", length=20)
    private String mobilePhone;

    @Column(name="mobile_phone_2", length=20)
    private String mobilePhone2;

    @Column(name="mobile_phone_3", length=20)
    private String mobilePhone3;


}
