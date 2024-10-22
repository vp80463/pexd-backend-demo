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
@Table(name="consumer_privacy_policy_result")
@Setter
@Getter
public class ConsumerPrivacyPolicyResult extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="consumer_privacy_policy_result_id", unique=true, nullable=false)
    private Long consumerPrivacyPolicyResultId;

    @Column(name="consumer_id")
    private Long consumerId;

    @Column(name="consumer_full_nm", length=180)
    private String consumerFullNm;

    @Column(name="mobile_phone", length=20)
    private String mobilePhone;

    @Column(name="agreement_result", length=40)
    private String agreementResult;

    @Column(name="result_upload_date", length=8)
    private String resultUploadDate;

    @Column(name="facility_cd", length=40)
    private String facilityCd;

    @Column(name="delete_flag", length=1)
    private String deleteFlag;

    @Column(name="consumer_retrieve", length=200)
    private String consumerRetrieve;


}
