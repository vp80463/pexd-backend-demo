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
@Table(name="service_package_category")
@Setter
@Getter
public class ServicePackageCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_package_category_id", unique=true, nullable=false)
    private Long servicePackageCategoryId;

    @Column(name="service_package_id")
    private Long servicePackageId;

    @Column(name="product_category_id")
    private Long productCategoryId;

    @Column(name="from_date", length=8)
    private String fromDate;

    @Column(name="to_date", length=8)
    private String toDate;


}
