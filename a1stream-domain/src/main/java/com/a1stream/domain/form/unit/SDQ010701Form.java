package com.a1stream.domain.form.unit;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

/**
*
* @author mid2259
*/
@Getter
@Setter
public class SDQ010701Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long supplierId;
    private String dateFrom;
    private String dateTo;
    private String manifestStatus;
    private String deliveryNoteNo;

}
