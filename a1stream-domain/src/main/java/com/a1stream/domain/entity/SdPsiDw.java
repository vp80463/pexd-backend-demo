package com.a1stream.domain.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.GenericGenerator;

import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="sd_psi_dw")
@Setter
@Getter
public class SdPsiDw {

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="site_id", length=10)
    private String siteId;

    @Column(name="facility_cd", length=40)
    private String facilityCd;

    @Column(name="facility_nm", length=256)
    private String facilityNm;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=256)
    private String productNm;

    @Column(name="color_cd", length=40)
    private String colorCd;

    @Column(name="color_nm", length=200)
    private String colorNm;

    @Column(name="psi_date", length=8)
    private String psiDate;

    @Column(name="begining_stock", precision=18, scale=2)
    private BigDecimal beginingStock = BigDecimal.ZERO;

    @Column(name="ymvn_in", precision=18, scale=2)
    private BigDecimal ymvnIn = BigDecimal.ZERO;

    @Column(name="whole_sales_in", precision=18, scale=2)
    private BigDecimal wholeSalesIn = BigDecimal.ZERO;

    @Column(name="transfer_in", precision=18, scale=2)
    private BigDecimal transferIn = BigDecimal.ZERO;

    @Column(name="retail_out", precision=18, scale=2)
    private BigDecimal retailOut = BigDecimal.ZERO;

    @Column(name="whole_sales_out", precision=18, scale=2)
    private BigDecimal wholeSalesOut = BigDecimal.ZERO;

    @Column(name="transfer_out", precision=18, scale=2)
    private BigDecimal transferOut = BigDecimal.ZERO;

    @Column(name="in_transit", precision=18, scale=2)
    private BigDecimal inTransit = BigDecimal.ZERO;

    @Column(name="create_datetime", length=14)
    private String createDatetime;
}
