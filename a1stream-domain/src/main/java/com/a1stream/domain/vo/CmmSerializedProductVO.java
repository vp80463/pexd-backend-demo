package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmSerializedProductVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serializedProductId;

    private String receivedDate;

    private String manufacturingDate;

    private Long productId;

    private Long facilityId;

    private Long locationInfoId;

    private String serviceSiteId;

    private String stuSiteId;

    private String stuDate;

    private BigDecimal stuPrice = BigDecimal.ZERO;

    private String evFlag;

    private String pdiFlag;

    private String pdiStartTime;

    private String pdiEndTime;

    private String pdiFinishDate;

    private String pdiPicNm;

    private String qualityStatus;

    private String stockStatus;

    private String salesStatus;

    private String refuseCall;

    private String originalSiteId;

    private Long originalFacilityId;

    private String frameNo;

    private String plateNo;

    private String barCode;

    private String engineNo;

    private String fromDate;

    private String toDate;

    public static CmmSerializedProductVO create() {
        CmmSerializedProductVO entity = new CmmSerializedProductVO();
        entity.setSerializedProductId(IdUtils.getSnowflakeIdWorker().nextId());
        return entity;
    }
}
