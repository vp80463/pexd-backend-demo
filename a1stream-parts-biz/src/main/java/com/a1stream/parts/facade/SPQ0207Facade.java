package com.a1stream.parts.facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPQ020701BO;
import com.a1stream.domain.form.parts.SPQ020701Form;
import com.a1stream.parts.service.SPQ0207Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;
/**
*
* 功能描述:Parts Cancel History Inquiry
*
* mid2330
* 2024年6月25日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/25   Liu Chaoran   New
*/
@Component
public class SPQ0207Facade {

    @Resource
    private SPQ0207Service spq0207Service;

    public Page<SPQ020701BO> findPartsCancelHisList(SPQ020701Form form, String siteId) {

        this.check(form);
        return spq0207Service.findPartsCancelHisList(form, siteId);
    }

    private void check(SPQ020701Form form) {

        //检查起始时间大于终止时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

        LocalDate dateFrom = LocalDate.parse(form.getDateFrom(), formatter);
        LocalDate dateTo = LocalDate.parse(form.getDateTo(), formatter);

        if (dateFrom.isAfter(dateTo)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("error.dateEqAfter", new String[] {
                                             CodedMessageUtils.getMessage("label.toDate"),
                                             CodedMessageUtils.getMessage("label.fromDate")}));
        }

        //检查parts
        if (StringUtils.isNotBlank(form.getParts()) && Nulls.isNull(form.getPartsId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.partsNo"),
                                             form.getParts(),
                                             CodedMessageUtils.getMessage("label.tableProduct")}));
        }
    }

    public List<SPQ020701BO> findPartsCancelHisExport(SPQ020701Form form, String siteId) {

        List<SPQ020701BO> partsList = spq0207Service.findPartsCancelHisExport(form, siteId);

        // 对返回的部件编号进行失焦操作
        for (SPQ020701BO bo : partsList) {
            String formattedPartNo = PartNoUtil.format(bo.getPartsCd());
            bo.setPartsCd(formattedPartNo);

            if (bo.getOrderDate() != null) {
                bo.setOrderDate(LocalDate.parse(bo.getOrderDate(), DateTimeFormatter.BASIC_ISO_DATE)
                        .format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT)));
            }

            if (bo.getCancelDate() != null) {
                bo.setCancelDate(LocalDate.parse(bo.getCancelDate(), DateTimeFormatter.BASIC_ISO_DATE)
                        .format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT)));
            }
        }
        return partsList;
    }
}
