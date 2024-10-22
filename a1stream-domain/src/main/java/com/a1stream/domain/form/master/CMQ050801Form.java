package com.a1stream.domain.form.master;

import java.util.List;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMQ050801Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long partsId;
    private Long pointId;
    private String parts;
    private String point;
    private List<String> productCategory;
    private String demandSource;

}
