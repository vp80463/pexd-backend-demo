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
@Table(name="mst_do_feature")
@Setter
@Getter
public class MstDoFeature extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="do_feature_id", unique=true, nullable=false)
    private Long doFeatureId;

    @Column(name="do_account", nullable=false, length=40)
    private String doAccount;

    @Column(name="do_acpass", nullable=false, length=40)
    private String doAcpass;

    @Column(name="do_area", nullable=false, length=40)
    private String doArea;

    @Column(name="do_convert", nullable=false, length=40)
    private String doConvert;

    @Column(name="do_patternsd", nullable=false, length=40)
    private String doPatternsd;

    @Column(name="do_patternspsv", nullable=false, length=40)
    private String doPatternspsv;

    @Column(name="do_serialsd", nullable=false, length=40)
    private String doSerialsd;

    @Column(name="do_serialspsv", nullable=false, length=40)
    private String doSerialspsv;

    @Column(name="do_username", nullable=false, length=40)
    private String doUsername;

    @Column(name="do_userpass", nullable=false, length=40)
    private String doUserpass;


}
