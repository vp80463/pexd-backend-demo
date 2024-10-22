/**
 *
 */
package com.a1stream.parts.facade;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.parts.SPM020202FunctionBO;
import com.a1stream.domain.form.parts.SPM020202Form;
import com.a1stream.domain.vo.InvoiceVO;
import com.a1stream.parts.service.SPM020202Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
* 功能描述: Sales Return History Inquiry
*
* mid2330
* 2024年7月2日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/07/02   Liu Chaoran   New
*/
@Component
public class SPM020202Facade {

    @Resource
    SPM020202Service spm020202Service;

    public List<SPM020202FunctionBO> getSalesReturnHistoryHeaderList(SPM020202Form form, String siteId) {

        //查询校验
        this.check(form,siteId);
        return spm020202Service.getSalesReturnHistoryHeaderList(form, siteId);
    }

    public List<SPM020202FunctionBO> getSalesReturnHistoryDetailList(SPM020202Form form, String siteId) {

        //带出detail信息
        return spm020202Service.getSalesReturnHistoryDetailList(form, siteId);
    }

    private void check(SPM020202Form form, String siteId) {
        //检查returnDate
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

        //检查InvoiceNo
        if (StringUtils.isNotBlank(form.getInvoiceNo())) {
            InvoiceVO invoiceVO = spm020202Service.searchInvoiceByInvoiceNoAndFromFacilityId(form.getInvoiceNo(), siteId, form.getPointId());
            if (ObjectUtils.isEmpty(invoiceVO)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10248"));
            }
        }

        //检查customer
        if (StringUtils.isNotBlank(form.getCustomer()) && Nulls.isNull(form.getCustomerId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.customer"),
                                             form.getCustomer(),
                                             CodedMessageUtils.getMessage("label.tableCustomerInfo")}));
        }
    }
}
