package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmUnitPromotionItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long unitPromotionItemId;

    private Long promotionListId;

    private Long facilityId;

    private Long productId;

    private Long cmmSerializedProductId;

    private String frameNo;

    private String stockMcFlag;

    public static CmmUnitPromotionItemVO create() {
        CmmUnitPromotionItemVO entity = new CmmUnitPromotionItemVO();
        entity.setUnitPromotionItemId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }

}
