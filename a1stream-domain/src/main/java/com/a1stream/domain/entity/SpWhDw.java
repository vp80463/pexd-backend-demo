package com.a1stream.domain.entity;

import org.hibernate.annotations.GenericGenerator;

import com.a1stream.common.constants.CommonConstants;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="sp_wh_dw")
@Setter
@Getter
public class SpWhDw {

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="site_id", length=10)
    private String siteId;

    @Column(name="account_month", length=6)
    private String accountMonth;

    @Column(name="target_year", length=4)
    private String targetYear;

    @Column(name="target_month", length=2)
    private String targetMonth;

    @Column(name="target_day", length=8)
    private String targetDay;

    @Column(name="facility_cd", length=40)
    private String facilityCd;

    @Column(name="facility_nm", length=256)
    private String facilityNm;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="stored_po_line", precision=10)
    private Integer storedPoLine = CommonConstants.INTEGER_ZERO;

    @Column(name="stored_so_return_line", precision=10)
    private Integer storedSoReturnLine = CommonConstants.INTEGER_ZERO;

    @Column(name="picking_so_line", precision=10)
    private Integer pickingSoLine = CommonConstants.INTEGER_ZERO;

    @Column(name="transfer_in_line", precision=10)
    private Integer transferInLine = CommonConstants.INTEGER_ZERO;

    @Column(name="transfer_out_line", precision=10)
    private Integer transferOutLine = CommonConstants.INTEGER_ZERO;

    @Column(name="adjust_in_line", precision=10)
    private Integer adjustInLine = CommonConstants.INTEGER_ZERO;

    @Column(name="adjust_out_line", precision=10)
    private Integer adjustOutLine = CommonConstants.INTEGER_ZERO;

    @Column(name="stocktaking_line", precision=10)
    private Integer stocktakingLine = CommonConstants.INTEGER_ZERO;

    @Column(name="create_datetime", length=14)
    private String createDatetime;
}
