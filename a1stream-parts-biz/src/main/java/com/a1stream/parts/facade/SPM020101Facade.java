package com.a1stream.parts.facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.parts.SPM020101BO;
import com.a1stream.domain.form.parts.SPM020101Form;
import com.a1stream.parts.service.SPM020101Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Component
public class SPM020101Facade {

    @Resource
    private SPM020101Service spm020101Service;


    public Page<SPM020101BO> searchSalesOrderList(SPM020101Form model) {

        if(!ObjectUtils.isEmpty(model.getDateFrom()) && !ObjectUtils.isEmpty(model.getDateTo())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);
            LocalDate localDateFrom = LocalDate.parse(model.getDateFrom(),formatter);
            LocalDate localDateTo = LocalDate.parse(model.getDateTo(),formatter);
            if (localDateFrom.isAfter(localDateTo)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("error.dateEqAfter", new String[] {CodedMessageUtils.getMessage("label.orderDateTo"),CodedMessageUtils.getMessage("label.orderDate")}));
            }

            // 检查日期是否超过六个月
            if (localDateFrom.plusMonths(CommonConstants.INTEGER_SIX).isBefore(localDateTo)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00339", new String[] {CodedMessageUtils.getMessage("label.orderDateFrom"),CodedMessageUtils.getMessage("label.orderDateTo")}));
            }
        }

        if(!ObjectUtils.isEmpty(model.getDateStart()) && !ObjectUtils.isEmpty(model.getDateEnd())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);
            LocalDate localDateStart = LocalDate.parse(model.getDateStart(),formatter);
            LocalDate localDateEnd = LocalDate.parse(model.getDateEnd(),formatter);
            if (localDateStart.isAfter(localDateEnd)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("error.dateEqAfter", new String[] {CodedMessageUtils.getMessage("label.orderDateTo"),CodedMessageUtils.getMessage("label.orderDate")}));
            }
        }

        if(ObjectUtils.isEmpty(model.getCustomerId())&& !ObjectUtils.isEmpty(model.getCustomer())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.customer"), model.getCustomer(), CodedMessageUtils.getMessage("label.tablePartyInfo")}));
        }

        return spm020101Service.searchSalesOrderList(model);
    }


}
