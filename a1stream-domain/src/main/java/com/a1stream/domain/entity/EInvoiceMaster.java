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
@Table(name="e_invoice_master")
@Setter
@Getter
public class EInvoiceMaster extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="db_id", unique=true, nullable=false)
    private Long dbId;

    @Column(name="account", length=40)
    private String account;

    @Column(name="acpass", length=40)
    private String acpass;

    @Column(name="area", length=20)
    private String area;

    @Column(name="convert", length=1)
    private String convert;

    @Column(name="patternsd", length=20)
    private String patternsd;

    @Column(name="patternspsv", length=20)
    private String patternspsv;

    @Column(name="serialsd", length=20)
    private String serialsd;

    @Column(name="serialspsv", length=20)
    private String serialspsv;

    @Column(name="user_name", length=40)
    private String userName;

    @Column(name="userpass", length=40)
    private String userpass;
}
