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
@Table(name="picking_list")
@Setter
@Getter
public class PickingList extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="picking_list_id", unique=true, nullable=false)
    private Long pickingListId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="picking_list_no", length=40)
    private String pickingListNo;

    @Column(name="picking_list_date", length=8)
    private String pickingListDate;

    @Column(name="started_date", length=8)
    private String startedDate;

    @Column(name="started_time", length=8)
    private String startedTime;

    @Column(name="finished_date", length=8)
    private String finishedDate;

    @Column(name="finished_time", length=8)
    private String finishedTime;

    @Column(name="inventory_transaction_type", length=40)
    private String inventoryTransactionType;

    @Column(name="product_classification", length=40)
    private String productClassification;


}
