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
@Table(name="organization_relation")
@Setter
@Getter
public class OrganizationRelation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="organization_relation_id", unique=true, nullable=false)
    private Long organizationRelationId;

    @Column(name="relation_type", nullable=false, length=40)
    private String relationType;

    @Column(name="from_organization_id")
    private Long fromOrganizationId;

    @Column(name="to_organization_id", nullable=false)
    private Long toOrganizationId;

    @Column(name="default_flag", length=1)
    private String defaultFlag;

    @Column(name="product_classification", length=40)
    private String productClassification;


}
