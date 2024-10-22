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
@Table(name="storing_line_item")
@Setter
@Getter
public class StoringLineItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="storing_line_item_id", unique=true, nullable=false)
    private Long storingLineItemId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="storing_line_id")
    private Long storingLineId;

    @Column(name="location_id")
    private Long locationId;

    @Column(name="location_cd", length=40)
    private String locationCd;

    @Column(name="instuction_qty", precision=18)
    private BigDecimal instuctionQty = BigDecimal.ZERO;

    @Column(name="stored_qty", precision=18)
    private BigDecimal storedQty = BigDecimal.ZERO;

    @Column(name="completed_date", length=8)
    private String completedDate;

    @Column(name="completed_time", length=8)
    private String completedTime;


}
