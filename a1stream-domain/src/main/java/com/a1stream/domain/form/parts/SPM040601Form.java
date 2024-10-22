package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.SPM040601BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM040601Form extends BaseForm{

    private static final long serialVersionUID = 1L;

    private Long supplierId;

    private String supplierCd;

    private String supplierNm;

    private Long pointId;

    private String pointCd;

    private String pointNm;

    private String deliveryPlanDate;

    private String type;

    private String memo;

    private List<SPM040601BO> gridDataList;

    private List<SPM040601BO> importList;

}
