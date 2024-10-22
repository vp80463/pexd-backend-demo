package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmSpecialClaimVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long specialClaimId;

    private String bulletinNo;

    private String campaignNo;

    private String campaignType;

    private String campaignTitle;

    private String description;

    private String effectiveDate;

    private String expiredDate;

    public static CmmSpecialClaimVO create() {
        CmmSpecialClaimVO entity = new CmmSpecialClaimVO();
        entity.setSpecialClaimId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }

}
