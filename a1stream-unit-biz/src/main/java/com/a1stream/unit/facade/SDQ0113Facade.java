package com.a1stream.unit.facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.unit.SDQ011301BO;
import com.a1stream.domain.bo.unit.SDQ011302BO;
import com.a1stream.domain.form.unit.SDQ011301Form;
import com.a1stream.domain.form.unit.SDQ011302Form;
import com.a1stream.unit.service.SDQ0113Service;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

@Component
public class SDQ0113Facade {

    @Resource
    private SDQ0113Service sdq0113Service;

    public List<SDQ011301BO> findStockInformationInquiry(SDQ011301Form form) {

        //查询校验
        this.check(form);
        return sdq0113Service.findStockInformationInquiry(form);
    }

    private void check(SDQ011301Form form) {

        if(StringUtils.isNotBlank(form.getModel()) && ObjectUtils.isEmpty(form.getModelId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.model"), form.getModel(), CodedMessageUtils.getMessage("label.productInformation")}));
        }
    }

    public List<SDQ011302BO> findExportByMotorcycle(SDQ011301Form form) {

        this.check(form);

        List<SDQ011302BO> exportList = sdq0113Service.findExportByMotorcycle(form);

        for (SDQ011302BO bo : exportList) {
            if (bo.getReceivedDate() != null) {
                //日期转为DDMMYYYY格式
                bo.setReceivedDate(LocalDate.parse(bo.getReceivedDate(), DateTimeFormatter.BASIC_ISO_DATE)
                                  .format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT)));
            }
        }
        return exportList;
    }

    public List<SDQ011302BO> findStockInformationInquiryDetail(SDQ011302Form form) {

        return sdq0113Service.findStockInformationInquiryDetail(form);
    }
}
