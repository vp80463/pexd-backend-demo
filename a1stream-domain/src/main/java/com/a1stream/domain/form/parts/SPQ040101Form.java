package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SPQ040101Form extends BaseForm{

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private String pointvl;
    private String orderNo;
    private String dateFrom;
    private String dateTo;
    private Long partId;
    private String newPart;
    private List<String> orderStatus;
    private String unfinishOnlySign;
    private String salesOrderNo;

    private boolean pageFlg = true;
}
