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
@Table(name="cmm_unit_promotion_item")
@Setter
@Getter
public class CmmUnitPromotionItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="unit_promotion_item_id", unique=true, nullable=false)
    private Long unitPromotionItemId;

    @Column(name="promotion_list_id", nullable=false)
    private Long promotionListId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="product_id", nullable=false)
    private Long productId;

    @Column(name="cmm_serialized_product_id")
    private Long cmmSerializedProductId;

    @Column(name="frame_no", length=40)
    private String frameNo;

    @Column(name="stock_mc_flag", length=1)
    private String stockMcFlag;

}
