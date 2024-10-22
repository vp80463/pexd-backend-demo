package com.a1stream.domain.entity;

import java.math.BigDecimal;

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
@Table(name="api_psi_info")
@Setter
@Getter
public class ApiPsiInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="psi_item_id", unique=true, nullable=false)
    private Long psiItemId;

    @Column(name="psi_id", nullable=false, length=20)
    private String psiId;

    @Column(name="psi_date", nullable=false, length=8)
    private String psiDate;

    @Column(name="point_cd", length=20)
    private String pointCd;

    @Column(name="model_id")
    private Long modelId;

    @Column(name="model_cd", length=40)
    private String modelCd;

    @Column(name="initial_inv", precision=18)
    private BigDecimal initialInv = BigDecimal.ZERO;

    @Column(name="ymvn_in", precision=18)
    private BigDecimal ymvnIn = BigDecimal.ZERO;

    @Column(name="wholesales_in", precision=18)
    private BigDecimal wholesalesIn = BigDecimal.ZERO;

    @Column(name="transfer_in", precision=18)
    private BigDecimal transferIn = BigDecimal.ZERO;

    @Column(name="retail_out", precision=18)
    private BigDecimal retailOut = BigDecimal.ZERO;

    @Column(name="wholesales_out", precision=18)
    private BigDecimal wholesalesOut = BigDecimal.ZERO;

    @Column(name="transfer_out", precision=18)
    private BigDecimal transferOut = BigDecimal.ZERO;

    @Column(name="in_transit", precision=18)
    private BigDecimal inTransit = BigDecimal.ZERO;


}
