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
@Table(name="mst_facility_relation")
@Setter
@Getter
public class MstFacilityRelation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="relation_id", unique=true, nullable=false)
    private Long relationId;

    @Column(name="from_facility_id", nullable=false)
    private Long fromFacilityId;

    @Column(name="to_facility_id", nullable=false)
    private Long toFacilityId;

    @Column(name="from_date", length=8)
    private String fromDate;

    @Column(name="to_date", length=8)
    private String toDate;

    @Column(name="comment", length=256)
    private String comment;

    @Column(name="facility_relation_type_id", length=40)
    private String facilityRelationTypeId;
}
