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
@Table(name="return_request_list")
@Setter
@Getter
public class ReturnRequestList extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="return_request_list_id", unique=true, nullable=false)
    private Long returnRequestListId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="request_status", length=40)
    private String requestStatus;

    @Column(name="recommend_date", length=8)
    private String recommendDate;

    @Column(name="request_date", length=8)
    private String requestDate;

    @Column(name="approved_date", length=8)
    private String approvedDate;

    @Column(name="expire_date", length=8)
    private String expireDate;

    @Column(name="product_classification", length=40)
    private String productClassification;


}
