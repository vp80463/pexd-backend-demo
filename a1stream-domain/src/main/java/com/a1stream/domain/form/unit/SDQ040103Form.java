package com.a1stream.domain.form.unit;

/**
*
* 功能描述:
*
* @author mid2215
*/
import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SDQ040103Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private String dateFrom;
    private String dateTo;
    private Long modelId;
    private Long consumerId;
    private String frameNo;
    private String plateNo;
    private String engineNo;
    private String colorName;
    private String mobileNo;
}
