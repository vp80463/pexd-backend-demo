package com.a1stream.domain.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmUnitPromotionModelVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long promotionModelId;

    private Long promotionListId;

    private Long productId;

    private String productCd;

    private String productNm;

    private String updateProgram;

    private Integer updateCount = 0;

    private String lastUpdatedBy;

    private LocalDateTime lastUpdated;

    private String createdBy;

    private LocalDateTime dateCreated;

    public static CmmUnitPromotionModelVO create() {
        CmmUnitPromotionModelVO entity = new CmmUnitPromotionModelVO();
        entity.setPromotionModelId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
