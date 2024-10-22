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
@Table(name="picking_item")
@Setter
@Getter
public class PickingItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="picking_item_id", unique=true, nullable=false)
    private Long pickingItemId;

    @Column(name="picking_list_id")
    private Long pickingListId;

    @Column(name="picking_list_no", length=40)
    private String pickingListNo;

    @Column(name="seq_no", length=3)
    private String seqNo;

    @Column(name="started_date", length=8)
    private String startedDate;

    @Column(name="started_time", length=8)
    private String startedTime;

    @Column(name="finished_date", length=8)
    private String finishedDate;

    @Column(name="finished_time", length=8)
    private String finishedTime;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=200)
    private String productNm;

    @Column(name="qty", precision=18)
    private BigDecimal qty = BigDecimal.ZERO;

    @Column(name="location_id")
    private Long locationId;

    @Column(name="location_cd", length=40)
    private String locationCd;

    @Column(name="delivery_order_id")
    private Long deliveryOrderId;

    @Column(name="delivery_order_item_id")
    private Long deliveryOrderItemId;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="facility_id")
    private Long facilityId;
}
