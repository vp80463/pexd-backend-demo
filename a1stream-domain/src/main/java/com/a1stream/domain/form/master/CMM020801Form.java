package com.a1stream.domain.form.master;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM020801Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private String role;
    private String status;
}