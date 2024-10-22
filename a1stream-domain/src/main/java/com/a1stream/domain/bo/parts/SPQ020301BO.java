package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ020301BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long invoiceId;
    
    private String customerCd;

    private String customerNm;

    private String point;

    private Long pointId;

    private String pointCd;

    private String pointNm;

    private Long customerId;

    private String invoiceNo;

    private String invoiceDate;

    private String invoiceType;

    private BigDecimal invoiceAmount;

    private BigDecimal invoiceAmountWithVAT;

    private Page<SPQ020302BO> tableDataList;

}
