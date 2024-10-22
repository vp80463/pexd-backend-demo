package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.logic.ValidateLogic;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPQ030401BO;
import com.a1stream.domain.form.parts.SPQ030401Form;
import com.a1stream.parts.service.SPQ0304Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
 * @author liu chaoran
 */
@Component
public class SPQ0304Facade {

    @Resource
    private SPQ0304Service spq0304Service;

    @Resource
    private ValidateLogic validateLogic;

    public List<SPQ030401BO> findPartsInOutHistoryList(SPQ030401Form model, String siteId) {

        //查询校验
        this.check(model);
        return spq0304Service.findPartsInOutHistoryList(model, siteId);
    }

    private void check(SPQ030401Form form) {

        //检查起始时间和终止时间
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

    public List<SPQ030401BO> exportPartsInOutHistory(SPQ030401Form model, String siteId) {

        List<SPQ030401BO> partsList = spq0304Service.findPartsInOutHistoryList(model, siteId);
        BigDecimal totalInQty = BigDecimal.ZERO;
        BigDecimal totalOutQty = BigDecimal.ZERO;
        // 对返回的部件编号进行失焦操作
        for (SPQ030401BO data : partsList) {
            String formattedPartNo = PartNoUtil.format(data.getPartsNo());
            data.setPartsNo(formattedPartNo);

            totalInQty = totalInQty.add(data.getInQty());
            totalOutQty = totalOutQty.add(data.getOutQty());

            if(data.getTransactionDate() != null) {

                LocalDate date = LocalDate.parse(data.getTransactionDate(), DateTimeFormatter.BASIC_ISO_DATE);

                // 将日期对象格式化为所需的字符串格式
                String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                // 将修改后的日期字符串设置回对象中
                data.setTransactionDate(formattedDate);
            }
        }

        // 创建底部合计行
        SPQ030401BO totalRow = new SPQ030401BO();
        totalRow.setTo("Total:");

        // 将totalInQty和totalOutQty转换为BigDecimal类型，并设置到totalRow中
        totalRow.setInQty(totalInQty);
        totalRow.setOutQty(totalOutQty);

        // 将底部合计行添加到数据集
        partsList.add(totalRow);

        return partsList;
    }
}
