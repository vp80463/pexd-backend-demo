package com.a1stream.domain.entity;

import org.hibernate.annotations.GenericGenerator;

import com.a1stream.common.constants.CommonConstants;
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
@Table(name="cmm_lead_management_2s")
@Setter
@Getter
public class CmmLeadManagement2s extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="lead_management_result_id", unique=true, nullable=false)
    private Long leadManagementResultId;

    @Column(name="facility_cd", length=40)
    private String facilityCd;

    @Column(name="scoring_data_export_date", length=8)
    private String scoringDataExportDate;

    @Column(name="lead_status", length=40)
    private String leadStatus;

    @Column(name="last_nm", length=60)
    private String lastNm;

    @Column(name="middle_nm", length=60)
    private String middleNm;

    @Column(name="first_nm", length=60)
    private String firstNm;

    @Column(name="province", length=256)
    private String province;

    @Column(name="district_town_city", length=256)
    private String districtTownCity;

    @Column(name="mobile_phone", length=20)
    private String mobilePhone;

    @Column(name="email", length=100)
    private String email;

    @Column(name="last_visit_dealer_cd", length=10)
    private String lastVisitDealerCd;

    @Column(name="last_visit_dealer_nm", length=256)
    private String lastVisitDealerNm;

    @Column(name="last_visit_date", length=8)
    private String lastVisitDate;

    @Column(name="frame_no", length=40)
    private String frameNo;

    @Column(name="plate_no", length=40)
    private String plateNo;

    @Column(name="current_mc_model", length=256)
    private String currentMcModel;

    @Column(name="fsc_use_history")
    private Integer fscUseHistory = CommonConstants.INTEGER_ZERO;

    @Column(name="current_voucher", length=40)
    private String currentVoucher;

    @Column(name="next_fsc_expire_date", length=8)
    private String nextFscExpireDate;

    @Column(name="last_oil_change_date", length=8)
    private String lastOilChangeDate;

    @Column(name="oil_nm", length=256)
    private String oilNm;

    @Column(name="contact_status", length=40)
    private String contactStatus;

    @Column(name="call_date_by_dealer", length=8)
    private String callDateByDealer;

    @Column(name="note", length=256)
    private String note;

    @Column(name="oil_flag", length=1)
    private String oilFlag;

    @Column(name="time_stamp", length=100)
    private String timeStamp;

    @Column(name="interested_model", length=256)
    private String interestedModel;

    @Column(name="interested_color", length=1000)
    private String interestedColor;


}
