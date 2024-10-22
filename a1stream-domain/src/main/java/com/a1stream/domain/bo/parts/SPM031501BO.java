package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM031501BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String lineNo;

    private String caseNo;

    private String partsNo;

    private Long partsId;

    private BigDecimal receiptedQty;

    private String location;

    private Long locationId;

    private Long binTypeId;

    private String locationType;

    private String locationTypeId;

    private String mainLocationSign;

    private BigDecimal instructionQty;

    private BigDecimal registerQty;

    private String setAsMainLocation;

    private Long storingLineId;

    private Long storingLineItemId;

    private Long storingListId;

    private Long receiptSlipItemId;

    private Long receiptSlipId;

    private BigDecimal storedQty;

    private BigDecimal frozenQty;

    private String inventoryTransactionType;

    private Boolean setAsMainLocationFlg = true;

    private String completedDate;

    private Integer rsiUpdateCount;

    private Integer rsUpdateCount;

    private Integer sliUpdateCount;

    private Integer slUpdateCount;
}
