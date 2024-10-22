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
@Table(name="cmm_promotion_order_zip_history")
@Setter
@Getter
public class CmmPromotionOrderZipHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="promotion_order_zip_history_id", unique=true, nullable=false)
    private Long promotionOrderZipHistoryId;

    @Column(name="promotion_list_id", nullable=false)
    private Long promotionListId;

    @Column(name="promotion_order_id", nullable=false)
    private Long promotionOrderId;

    @Column(name="zip_path", length=256)
    private String zipPath;

    @Column(name="zip_nm", length=40)
    private String zipNm;

    @Column(name="backup_path", length=256)
    private String backupPath;


}
