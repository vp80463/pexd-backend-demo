package com.a1stream.domain.form.parts;

import java.math.BigDecimal;
import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.SPM030301BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM030301Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String point;

    private String lineNo;

    private Long storingLineId;

    private List<SPM030301BO> insertData;

    private List<SPM030301BO> updateData;

    private BigDecimal receiptQty;

    private BigDecimal registerQty;

    private BigDecimal onFrozenQty;

    private String partsNo;

    private Long partsId;
}
