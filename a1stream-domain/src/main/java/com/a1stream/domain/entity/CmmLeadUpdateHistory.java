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
@Table(name="cmm_lead_update_history")
@Setter
@Getter
public class CmmLeadUpdateHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="lead_update_history_id", unique=true, nullable=false)
    private Long leadUpdateHistoryId;

    @Column(name="dealer_cd", nullable=false, length=20)
    private String dealerCd;

    @Column(name="telephone", length=20)
    private String telephone;

    @Column(name="customer_nm", length=256)
    private String customerNm;

    @Column(name="call_date_by_dealer", length=8)
    private String callDateByDealer;

    @Column(name="contact_status", length=40)
    private String contactStatus;

    @Column(name="mc_flag", length=1)
    private String mcFlag;

    @Column(name="oil_flag", length=1)
    private String oilFlag;

    @Column(name="lead_management_result_id", nullable=false)
    private Long leadManagementResultId;

    @Column(name="lead_status", length=40)
    private String leadStatus;

    @Column(name="frame_no", length=40)
    private String frameNo;

    @Column(name="plate_no", length=40)
    private String plateNo;


}
