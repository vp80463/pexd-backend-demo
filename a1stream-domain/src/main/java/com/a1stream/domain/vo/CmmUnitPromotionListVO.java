package com.a1stream.domain.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.a1stream.common.constants.CommonConstants;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class CmmUnitPromotionListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long promotionListId;

    private String promotionCd;

    private String promotionNm;

    private String invoice;

    private String registrationCard;

    private String giftReceipt;

    private String giftNm1;

    private String giftNm2;

    private String giftNm3;

    private Integer giftMax = CommonConstants.INTEGER_ZERO;

    private String luckyDrawVoucher;

    private String idCard;

    private String othersFlag;

    private String fromDate;

    private String toDate;

    private String uploadEndDate;

    private String giftReceiptSamplePath;

    private String winner;

    private String modifyFlag;

    private String batchFlag;

    private String promotionRetrieve;

    private String updateProgram;

    private Integer updateCount;

    private String lastUpdatedBy;

    private LocalDateTime lastUpdated;

    private String createdBy;

    private LocalDateTime dateCreated;

    public static CmmUnitPromotionListVO create() {
        CmmUnitPromotionListVO entity = new CmmUnitPromotionListVO();
        entity.setPromotionListId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
