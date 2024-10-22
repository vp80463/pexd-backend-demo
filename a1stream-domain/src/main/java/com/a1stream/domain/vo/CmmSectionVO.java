package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmSectionVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long productSectionId;

    private String productSectionCd;

    private String productSectionNm;

    public static CmmSectionVO create() {
        CmmSectionVO entity = new CmmSectionVO();
        entity.setProductSectionId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
