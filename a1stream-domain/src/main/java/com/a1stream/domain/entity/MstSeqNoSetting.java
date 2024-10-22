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
@Table(name="mst_seq_no_setting")
@Setter
@Getter
public class MstSeqNoSetting extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="seq_no_setting_id", unique=true, nullable=false)
    private Long seqNoSettingId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="seq_no_type", nullable=false, length=40)
    private String seqNoType;

    @Column(name="formula", nullable=false, length=100)
    private String formula;

    @Column(name="identifying_code_formula", nullable=false, length=100)
    private String identifyingCodeFormula;

    @Column(name="start_number", nullable=false)
    private Integer startNumber = CommonConstants.INTEGER_ZERO;

    @Column(name="max_number", nullable=false)
    private Integer maxNumber = CommonConstants.INTEGER_ZERO;

    @Column(name="description", length=40)
    private String description;

    @Column(name="complementing_char", length=1)
    private String complementingChar;

    @Column(name="seq_no_length", nullable=false)
    private Integer seqNoLength = CommonConstants.INTEGER_ZERO;

    @Column(name="total_length", nullable=false)
    private Integer totalLength = CommonConstants.INTEGER_ZERO;


}
