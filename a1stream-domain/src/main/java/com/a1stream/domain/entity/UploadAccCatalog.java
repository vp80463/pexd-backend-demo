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
@Table(name="upload_acc_catalog")
@Setter
@Getter
public class UploadAccCatalog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="upload_acc_catalog_id", unique=true, nullable=false)
    private Long uploadAccCatalogId;

    @Column(name="parameter_value")
    private String parameterValue;

    @Column(name="parameter_type_id", length=40)
    private String parameterTypeId;

}
