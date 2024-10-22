package com.a1stream.unit.facade;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.logic.ValidateLogic;
import com.a1stream.domain.bo.unit.SDQ070601BO;
import com.a1stream.domain.form.unit.SDQ070601Form;
import com.a1stream.unit.service.SDQ0706Service;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Component
public class SDQ0706Facade {

    @Resource
    private SDQ0706Service sdq0706Service;

    @Resource
    ValidateLogic validateLogic;

    public Page<SDQ070601BO> findSdPsiDwList(SDQ070601Form form) {

        validateLogic.validateDateRange(form.getDateFrom(), form.getDateTo(), CommonConstants.INT_SIX);

        return sdq0706Service.findSdPsiDwList(form);
    }
}
