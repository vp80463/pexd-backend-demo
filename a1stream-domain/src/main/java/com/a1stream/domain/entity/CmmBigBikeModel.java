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
@Table(name="cmm_big_bike_model")
@Setter
@Getter
public class CmmBigBikeModel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="big_bike_model_id", unique=true, nullable=false)
    private Long bigBikeModelId;

    @Column(name="coup_ctg_cd", length=10)
    private String coupCtgCd;

    @Column(name="pl_cd", length=10)
    private String plCd;

    @Column(name="coup_ctg_level")
    private Long coupCtgLevel;

    @Column(name="user_month_from", length=40)
    private String userMonthFrom;

    @Column(name="user_month_to", length=40)
    private String userMonthTo;

    @Column(name="mileage_from", length=40)
    private String mileageFrom;

    @Column(name="mileage_to", length=40)
    private String mileageTo;

    @Column(name="model_cd", length=40)
    private String modelCd;


}
