package com.a1stream.domain.form.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.parts.SPM030901BO;

import lombok.Getter;
import lombok.Setter;

/**
*
* 功能描述: 库存盘点时真实库存录入
*
* @author mid2215
*/
@Getter
@Setter
public class SPM030901Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long productStockTakingId;
    private String rangeType;
    private String point;
    private Long pointId;
    private String location;
    private Long locationId;
    private String locationTypeCode;
    private String wz;
    private Long wzId;
    private BigDecimal seqNoFrom;
    private BigDecimal seqNoTo;
    private Long partsId;
    private String parts;
    private String partsName;
    private BigDecimal actualQty;
    private BigDecimal currentAverageCost;
    private String startedDate;
    private String startedTime;
    private String status;
    private String action;
    private String noneInputOnly;
    private String showDiff;

    private BaseTableData<SPM030901BO> stockData;
}
