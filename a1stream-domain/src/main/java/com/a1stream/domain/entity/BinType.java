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
@Table(name="bin_type")
@Setter
@Getter
public class BinType extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="bin_type_id", unique=true, nullable=false)
    private Long binTypeId;

    @Column(name="bin_type_cd", length=10)
    private String binTypeCd;

    @Column(name="description", nullable=false, length=256)
    private String description;

    @Column(name="length")
    private Long length;

    @Column(name="width")
    private Long width;

    @Column(name="height")
    private Long height;

}
