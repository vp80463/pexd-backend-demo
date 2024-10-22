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
@Table(name="storing_list")
@Setter
@Getter
public class StoringList extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="storing_list_id", unique=true, nullable=false)
    private Long storingListId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="storing_list_no", length=40)
    private String storingListNo;

    @Column(name="storing_pic_id")
    private Long storingPicId;

    @Column(name="storing_pic_nm", length=60)
    private String storingPicNm;

    @Column(name="receipt_date", length=8)
    private String receiptDate;

    @Column(name="instruction_date", length=8)
    private String instructionDate;

    @Column(name="instruction_time", length=8)
    private String instructionTime;

    @Column(name="completed_date", length=8)
    private String completedDate;

    @Column(name="completed_time", length=8)
    private String completedTime;

    @Column(name="inventory_transaction_type", length=40)
    private String inventoryTransactionType;

    @Column(name="product_classification", length=40)
    private String productClassification;


}
