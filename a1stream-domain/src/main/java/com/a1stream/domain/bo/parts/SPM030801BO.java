package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM030801BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long locationId;

    private Long productId;

    private Long workzoneId;

    private BigDecimal qty;

    private BigDecimal cost;

    private Long productInventoryId;
}
