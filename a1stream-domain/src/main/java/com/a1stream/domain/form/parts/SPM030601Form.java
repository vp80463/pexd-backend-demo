package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.SPM030601BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM030601Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String fromPoint;

    private Long fromPointId;

    private String toPoint;

    private Long toPointId;

    private String printFlag;

    private List<SPM030601BO> gridList;

}
