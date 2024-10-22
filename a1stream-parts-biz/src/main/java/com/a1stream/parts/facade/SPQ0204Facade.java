/**
 *
 */
package com.a1stream.parts.facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPQ020401BO;
import com.a1stream.domain.bo.parts.SPQ020402BO;
import com.a1stream.domain.bo.parts.SPQ020403BO;
import com.a1stream.domain.form.parts.SPQ020401Form;
import com.a1stream.domain.vo.InvoiceVO;
import com.a1stream.parts.service.SPQ0204Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Component
public class SPQ0204Facade {

    @Resource
    SPQ0204Service spq0204Service;

    public SPQ020401BO searchInvoiceInfo(SPQ020401Form model) {

        this.validateData(model);

        SPQ020401BO result = new SPQ020401BO();

        List<SPQ020402BO> summaryContent = spq0204Service.searchInvoiceInfo(model);
        result.setSummaryContent(summaryContent);

        List<SPQ020403BO> detailContent = spq0204Service.searchInvoiceDetailInfo(model);
        for (SPQ020403BO bo : detailContent) {
            bo.setParts(PartNoUtil.format(bo.getPartsNo()));
        }
        result.setDetailContent(detailContent);

        return result;
    }

    public SPQ020401BO searchInvoiceInfoExport(SPQ020401Form model) {

        SPQ020401BO result = new SPQ020401BO();

        List<SPQ020402BO> summaryContent = spq0204Service.searchInvoiceInfo(model);
        for (SPQ020402BO bo : summaryContent) {
            bo.setOrderDate(LocalDate.parse(bo.getOrderDate(), DateTimeFormatter.BASIC_ISO_DATE).format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT)));
            bo.setInvoiceDate(LocalDate.parse(bo.getInvoiceDate(), DateTimeFormatter.BASIC_ISO_DATE).format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT)));
        }
        result.setSummaryContent(summaryContent);

        List<SPQ020403BO> detailContent = spq0204Service.searchInvoiceDetailInfo(model);
        for (SPQ020403BO bo : detailContent) {
            bo.setParts(PartNoUtil.format(bo.getPartsNo()));
            bo.setOrderDate(LocalDate.parse(bo.getOrderDate(), DateTimeFormatter.BASIC_ISO_DATE).format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT)));
            bo.setInvoiceDate(LocalDate.parse(bo.getInvoiceDate(), DateTimeFormatter.BASIC_ISO_DATE).format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT)));
        }
        result.setDetailContent(detailContent);

        return result;
    }

    public void confirmInvoiceInfo(SPQ020401Form model) {

        List<InvoiceVO> result = new ArrayList<>();
        List<SPQ020402BO> updateList = model.getContent().getUpdateRecords();
        //将待修改的数据
        Map<Long, SPQ020402BO> invoiceMap = updateList.stream()
                .collect(Collectors.toMap(SPQ020402BO::getInvoiceId, obj -> obj));

        if (!invoiceMap.isEmpty()) {
            List<InvoiceVO> invoiceVOs = spq0204Service.findInvoiceById(invoiceMap.keySet());
            for (InvoiceVO invoiceVO : invoiceVOs) {
                SPQ020402BO bo = invoiceMap.get(invoiceVO.getInvoiceId());
                invoiceVO.setDeliveryAddress(bo.getAddress());
                invoiceVO.setCustomerNm(bo.getCustomer());
                invoiceVO.setVatMobilePhone(bo.getPhoneNo());
                invoiceVO.setSerialNo(bo.getSerialNo());
                invoiceVO.setTaxCode(bo.getTaxCode());
                invoiceVO.setVatNo(bo.getVatNo());
                result.add(invoiceVO);
            }
            spq0204Service.saveInvoiceInfo(result);
        }
    }

    private void validateData(SPQ020401Form model){

        //验证date
        if(!ObjectUtils.isEmpty(model.getDateFrom()) && !ObjectUtils.isEmpty(model.getDateTo())) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);
            LocalDate localDateFrom = LocalDate.parse(model.getDateFrom().substring(CommonConstants.INTEGER_ZERO, CommonConstants.INTEGER_EIGHT),formatter);
            LocalDate localDateTo = LocalDate.parse(model.getDateTo().substring(CommonConstants.INTEGER_ZERO, CommonConstants.INTEGER_EIGHT),formatter);

            if (localDateFrom.isAfter(localDateTo)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("error.dateEqAfter", new String[] {CodedMessageUtils.getMessage("label.orderDateTo"),CodedMessageUtils.getMessage("label.orderDate")}));
            }

            // 计算日期差
            long daysDifference = ChronoUnit.DAYS.between(localDateFrom, localDateTo);

            if (daysDifference > CommonConstants.INTEGER_THIRTY) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10218"));
            }
        }

        //验证point是否存在
        if(ObjectUtils.isEmpty(model.getPointId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.point"), model.getPoint(), CodedMessageUtils.getMessage("label.tablePartyFacility")}));
        }

    }

}
