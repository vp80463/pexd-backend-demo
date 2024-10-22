package com.a1stream.unit.facade;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.logic.ValidateLogic;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.domain.bo.unit.SDQ040103BO;
import com.a1stream.domain.form.unit.SDQ040103Form;
import com.a1stream.unit.service.SDQ0401Service;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Component
public class SDQ0401Facade {

    @Resource
    private SDQ0401Service sdq0401Service;

    @Resource
    private ValidateLogic validateLogic;

    public Page<SDQ040103BO> pageWarrantyCardInfo(SDQ040103Form form, String siteId) {

        validateLogic.validateDateRange(form.getDateFrom(), form.getDateTo(), CommonConstants.INT_SIX);

        return sdq0401Service.getWarrantyCardPage(form, siteId);
    }

    public List<SDQ040103BO> listWarrantyCardInfo(SDQ040103Form form, String siteId) {

        validateLogic.validateDateRange(form.getDateFrom(), form.getDateTo(), CommonConstants.INT_SIX);

        List<SDQ040103BO> exportData = sdq0401Service.getWarrantyCardList(form, siteId);
        for(SDQ040103BO item : exportData) {
            item.setRegistrationDate(ComUtil.changeFormat(item.getRegistrationDate()));
            item.setOrderDate(ComUtil.changeFormat(item.getOrderDate()));
        }

        return exportData;
    }
}
