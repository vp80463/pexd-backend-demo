package com.a1stream.domain.form.unit;

import java.util.List;

/**
*
* 功能描述:
*
* @author mid2215
*/
import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.unit.CMM090101BO;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CMM090101Form extends BaseForm{

    private static final long serialVersionUID = 1L;

    private Long modelId;

    private String priceType;
    
    private List<CMM090101BO> modelPriceData;

    private List<CMM090101BO> importList;
}
