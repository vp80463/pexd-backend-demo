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
@Table(name="purchase_recommendation")
@Setter
@Getter
public class PurchaseRecommendation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="purchase_recommendation_id", unique=true, nullable=false)
    private Long purchaseRecommendationId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="organization_id")
    private Long organizationId;

    @Column(name="order_type_id", length=40)
    private String orderTypeId;

    @Column(name="recommend_qty", precision=18, scale = 2)
    private BigDecimal recommendQty = BigDecimal.ZERO;

}
