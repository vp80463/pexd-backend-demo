package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class MstProductRelationVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long productRelationId;

    private String relationType;

    private Long fromProductId;

    private Long toProductId;

    private String fromDate;

    private String toDate;

    private BigDecimal fromQty = BigDecimal.ZERO;

    private BigDecimal toQty = BigDecimal.ZERO;

    public static MstProductRelationVO create() {
        MstProductRelationVO entity = new MstProductRelationVO();
        entity.setProductRelationId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }

}
