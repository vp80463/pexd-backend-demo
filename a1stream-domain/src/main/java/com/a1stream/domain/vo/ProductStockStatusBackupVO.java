package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ProductStockStatusBackupVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long productStockStatusId;

    private Long facilityId;

    private Long productId;

    private String productStockStatusType;

    private BigDecimal qty = BigDecimal.ZERO;

    private String productClassification;

    private String backupDate;
}
