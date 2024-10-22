package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM030301BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String location;

    private Long locationId;

    private Long binTypeId;

    private String locationType;

    private String locationTypeCd;

    private BigDecimal qty;

    private Boolean delFlg = true;

    private Long storingLineItemId;

    private Long storingListId;

    private Long receiptSlipItemId;

    private Long receiptSlipId;

    private Integer rsiUpdateCount;

    private Integer rsUpdateCount;

    private Integer sliUpdateCount;

    private Integer slUpdateCount;

}
