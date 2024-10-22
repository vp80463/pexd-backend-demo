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
@Table(name="cmm_special_claim_problem")
@Setter
@Getter
public class CmmSpecialClaimProblem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="special_claim_problem_id", unique=true, nullable=false)
    private Long specialClaimProblemId;

    @Column(name="special_claim_id")
    private Long specialClaimId;

    @Column(name="problem_category", length=40)
    private String problemCategory;

    @Column(name="problem_cd", length=40)
    private String problemCd;


}
