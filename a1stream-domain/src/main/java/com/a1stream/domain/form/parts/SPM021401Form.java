package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM021401Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private List<String> status;
}
