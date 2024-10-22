package com.a1stream.parts.facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPQ020201BO;
import com.a1stream.domain.form.parts.SPQ020201Form;
import com.a1stream.parts.service.SPQ0202Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;
/**
*
* 功能描述:Sales Order Inquiry (By Customer)
*
* mid2330
* 2024年6月24日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/26   Liu Chaoran   New
*/
@Component
public class SPQ0202Facade {

    @Resource
    private SPQ0202Service spq0202Service;

    public Page<SPQ020201BO> findSalesOrderPartsList(SPQ020201Form form, String siteId) {

        this.check(form);
        return spq0202Service.findSalesOrderPartsList(form, siteId);
    }

    private void check(SPQ020201Form form) {

        //检查起始时间大于终止时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

        LocalDate dateFrom = LocalDate.parse(form.getDateFrom(), formatter);
        LocalDate dateTo = LocalDate.parse(form.getDateTo(), formatter);

        if (dateFrom.isAfter(dateTo)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("error.dateEqAfter", new String[] {
                                             CodedMessageUtils.getMessage("label.toDate"),
                                             CodedMessageUtils.getMessage("label.fromDate")}));
        }

        if (dateFrom.plusMonths(CommonConstants.INTEGER_SIX).isBefore(dateTo)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00339", new String[] {CodedMessageUtils.getMessage("label.orderDateFrom"),CodedMessageUtils.getMessage("label.orderDateTo")}));
        }

        //检查parts
        if (StringUtils.isNotBlank(form.getParts()) && Nulls.isNull(form.getPartsId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.partsNo"),
                                             form.getParts(),
                                             CodedMessageUtils.getMessage("label.tableProduct")}));
        }
    }

    public List<SPQ020201BO> findSalesOrderPartsExportList(SPQ020201Form form, String siteId) {

        //导出数据查询
        List<SPQ020201BO> partsList = spq0202Service.findSalesOrderPartsExportList(form, siteId);

        //数据处理
        for (SPQ020201BO bo : partsList) {
            bo.setOrderPartsCd(PartNoUtil.format(bo.getOrderPartsCd()));
            bo.setAllocatedPartsCd(PartNoUtil.format(bo.getAllocatedPartsCd()));

            if (bo.getOrderDate() != null) {
                bo.setOrderDate(LocalDate.parse(bo.getOrderDate(), DateTimeFormatter.BASIC_ISO_DATE)
                        .format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT)));
            }
        }
        return partsList;
    }
}
