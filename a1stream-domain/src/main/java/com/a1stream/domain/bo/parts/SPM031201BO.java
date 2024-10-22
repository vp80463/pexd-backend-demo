package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM031201BO extends BaseBO {
	private static final long serialVersionUID = 1L;

    private BigDecimal frozenQty;
    private String mainLocation;
    private BigDecimal fromLocationQty;
    private Long toLocationId;
    private String toLocation;
    private String releaseType;

}
