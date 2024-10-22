package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.domain.bo.parts.SPQ020101BO;
import com.a1stream.domain.form.parts.SPQ020101Form;
import com.a1stream.parts.service.SPQ0201Service;
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
*  1.0    2024/06/24   Liu Chaoran   New
 */
@Component
public class SPQ0201Facade {

    @Resource
    private SPQ0201Service spq0201Service;

    @Resource
    private HelperFacade helperFacade;

    public Page<SPQ020101BO> findSalesOrderCustomerList(SPQ020101Form form, String siteId) {

        //校验
        this.check(form);

        //分页查询
        Page<SPQ020101BO> resultList = spq0201Service.findSalesOrderCustomerList(form, siteId);

        //sourceType编码转code
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(ProductClsType.CODE_ID);
        for (SPQ020101BO bo : resultList) {
            bo.setSourceType(codeMap.get(bo.getSourceTypeCode()));
        }

        return resultList;
    }

    private void check(SPQ020101Form form) {

        //检查起始时间是否大于结束时间
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

        //检查customer
        if (StringUtils.isNotBlank(form.getCustomer()) && Nulls.isNull(form.getCustomerId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.customer"),
                                             form.getCustomer(),
                                             CodedMessageUtils.getMessage("label.tableCustomerInfo")}));
        }
    }

    public List<SPQ020101BO> findSalesOrderCustomerExportList(SPQ020101Form form, String siteId) {

        //导出数据查询
        List<SPQ020101BO> exportList = spq0201Service.findSalesOrderCustomerExportList(form, siteId);

        BigDecimal totalAllLines = BigDecimal.ZERO;
        BigDecimal totalOrderLines = BigDecimal.ZERO;
        BigDecimal totalOrderAmt = BigDecimal.ZERO;
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(ProductClsType.CODE_ID);
        for (SPQ020101BO bo: exportList) {

            if (bo.getOrderDate() != null) {
                LocalDate date = LocalDate.parse(bo.getOrderDate(), DateTimeFormatter.BASIC_ISO_DATE);

                String formattedDate = date.format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT));

                bo.setOrderDate(formattedDate);
            }

            bo.setSourceType(codeMap.get(bo.getSourceType()));

            totalAllLines = totalAllLines.add(bo.getAllocatedLines() != null ? bo.getAllocatedLines() : BigDecimal.ZERO);
            totalOrderLines = totalOrderLines.add(bo.getOrderLines() != null ? bo.getOrderLines() : BigDecimal.ZERO);
            totalOrderAmt = totalOrderAmt.add(bo.getOrderAmt() != null ? bo.getOrderAmt() : BigDecimal.ZERO);
        }

        //底部合计处理
        SPQ020101BO totalRow = new SPQ020101BO();
        totalRow.setOrderDate("Total:");
        totalRow.setAllocatedLines(totalAllLines);
        totalRow.setOrderLines(totalOrderLines);
        totalRow.setOrderAmt(totalOrderAmt);
        exportList.add(totalRow);

        return exportList;
    }
}
