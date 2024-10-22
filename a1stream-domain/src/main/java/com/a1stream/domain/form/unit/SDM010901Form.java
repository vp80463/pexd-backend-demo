package com.a1stream.domain.form.unit;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.unit.SDM010901BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM010901Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private List<SDM010901BO> pointList;

    private List<SDM010901BO> importList;

    private Long pointId;

    private String pointCd;

    private String returnDate;
}
