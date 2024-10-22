package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM030501BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String fromLocation;

    private Long fromLocationId;

    private String fromLocationType;

    private String fromLocationTypeId;

    private String mainLocationSign;

    private BigDecimal stockQty;

    private BigDecimal movementQty = BigDecimal.ZERO;

    private String toLocation;

    private Long toLocationId;

    private Long toBinTypeId;

    private String toLocationType;

    private String toLocationTypeId;
    
    private String setAsMainLocation;

    private Boolean setAsMainLocationFlg = true;
    
    private Long productInventoryId;

}
