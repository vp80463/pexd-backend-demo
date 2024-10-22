package com.a1stream.domain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.a1stream.common.constants.CommonConstants;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="cmm_unit_promotion_list")
@Setter
@Getter
public class CmmUnitPromotionList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="promotion_list_id", unique=true, nullable=false)
    private Long promotionListId;

    @Column(name="promotion_cd", nullable=false, length=20)
    private String promotionCd;

    @Column(name="promotion_nm", nullable=false, length=256)
    private String promotionNm;

    @Column(name="invoice", length=1)
    private String invoice;

    @Column(name="registration_card", length=1)
    private String registrationCard;

    @Column(name="gift_receipt", length=1)
    private String giftReceipt;

    @Column(name="gift_nm1", length=1000)
    private String giftNm1;

    @Column(name="gift_nm2", length=1000)
    private String giftNm2;

    @Column(name="gift_nm3", length=1000)
    private String giftNm3;

    @Column(name="gift_max")
    private Integer giftMax = CommonConstants.INTEGER_ZERO;

    @Column(name="lucky_draw_voucher", length=1)
    private String luckyDrawVoucher;

    @Column(name="id_card", length=1)
    private String idCard;

    @Column(name="others_flag", length=1)
    private String othersFlag;

    @Column(name="from_date", length=8)
    private String fromDate;

    @Column(name="to_date", length=8)
    private String toDate;

    @Column(name="upload_end_date", length=8)
    private String uploadEndDate;

    @Column(name="gift_receipt_sample_path", length=256)
    private String giftReceiptSamplePath;

    @Column(name="winner", length=1)
    private String winner;

    @Column(name="modify_flag", length=1)
    private String modifyFlag;

    @Column(name="batch_flag", length=1)
    private String batchFlag;

    @Column(name="promotion_retrieve", length=600)
    private String promotionRetrieve;

    @Column(name="update_program", length=20)
    private String updateProgram;

    @Version
    @Column(name = "update_count")
    private Integer updateCount;

    @LastModifiedBy
    @Column(name = "last_updated_by")
    private String lastUpdatedBy;

    @LastModifiedDate
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @CreatedBy
    @Column(name = "created_by", length=60)
    private String createdBy;

    @CreatedDate
    @Column(name = "date_created", length=60)
    private LocalDateTime dateCreated;
}
