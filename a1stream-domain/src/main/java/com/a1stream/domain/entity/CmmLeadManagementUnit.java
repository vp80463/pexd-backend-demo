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
@Table(name="cmm_lead_management_unit")
@Setter
@Getter
public class CmmLeadManagementUnit extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="lead_management_result_id", unique=true, nullable=false)
    private Long leadManagementResultId;

    @Column(name="dealer_cd", nullable=false, length=20)
    private String dealerCd;

    @Column(name="telephone", length=20)
    private String telephone;

    @Column(name="customer_nm", length=256)
    private String customerNm;

    @Column(name="province", length=256)
    private String province;

    @Column(name="district_town_city", length=256)
    private String districtTownCity;

    @Column(name="interested_model", length=256)
    private String interestedModel;

    @Column(name="interested_color", length=1000)
    private String interestedColor;

    @Column(name="lead_status", length=40)
    private String leadStatus;

    @Column(name="contact_status", length=40)
    private String contactStatus;

    @Column(name="source", length=20)
    private String source;

    @Column(name="contact_date_from_customer", length=8)
    private String contactDateFromCustomer;

    @Column(name="call_date_by_dealer", length=8)
    private String callDateByDealer;

    @Column(name="timestamp", length=30)
    private String timestamp;

    @Column(name="estimate_time_to_buy", length=60)
    private String estimateTimeToBuy;

    @Column(name="remark", length=1000)
    private String remark;

    @Column(name="lead_type", length=40)
    private String leadType;
}
