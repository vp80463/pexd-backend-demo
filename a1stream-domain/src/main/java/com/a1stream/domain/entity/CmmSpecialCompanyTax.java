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
@Table(name="cmm_special_company_tax")
@Setter
@Getter
public class CmmSpecialCompanyTax extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="special_company_tax_id", unique=true, nullable=false)
    private Long specialCompanyTaxId;

    @Column(name="special_company_tax_cd", length=100)
    private String specialCompanyTaxCd;

    @Column(name="special_company_tax_nm", length=40)
    private String specialCompanyTaxNm;

    @Column(name="address", length=256)
    private String address;


}
