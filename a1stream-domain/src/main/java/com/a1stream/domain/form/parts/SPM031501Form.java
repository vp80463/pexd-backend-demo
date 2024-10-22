package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.SPM031501BO;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:
*
* mid2287
* 2024年6月13日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/13   Wang Nan      New
*/

@Getter
@Setter
public class SPM031501Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;

    private String point;

    private String receiptNo;

    private List<SPM031501BO> gridDataList;
}
