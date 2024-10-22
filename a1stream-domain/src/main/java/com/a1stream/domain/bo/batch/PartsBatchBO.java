package com.a1stream.domain.bo.batch;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsBatchBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String parametervalue;

    private String dateto;

    private Long facilityid;

    private String facilitycd;

    private Long fromproductid;

    private Long productid;

    private Long toProductid;

    private String registrationdate;

    private String firstorderdate;

    private Long categoryid;

    private BigDecimal cost;

    private String salesstatustype;

    private String stringvalue;

    private String systemparametertypeid;

    private Long productcategoryid;

    private String ropqtype;
}
