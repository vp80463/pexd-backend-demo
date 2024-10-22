package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ReorderGuidelineVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long reorderGuidelineId;

    private Long productId;

    private BigDecimal reorderPoint = BigDecimal.ZERO;

    private BigDecimal reorderQty = BigDecimal.ZERO;

    private String ropRoqManualFlag;

    private Integer partsLeadtime = CommonConstants.INTEGER_ZERO;

    private Long facilityId;

    public static ReorderGuidelineVO create() {
        ReorderGuidelineVO entity = new ReorderGuidelineVO();
        entity.setReorderGuidelineId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
