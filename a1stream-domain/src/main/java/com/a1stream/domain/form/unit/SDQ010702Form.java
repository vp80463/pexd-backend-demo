package com.a1stream.domain.form.unit;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDQ010702Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long receiptManifestId;
    private Long pointId;
}
