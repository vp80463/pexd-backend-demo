package com.a1stream.domain.form.master;

import java.util.List;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM050101Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String parts;
    private Long partsId;
    private String partsCd;
    private String partsNm;
    private Long brand;
    private List<Long> productCategory;
}
