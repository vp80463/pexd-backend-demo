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
import com.a1stream.domain.bo.parts.SPQ030201BO;
import com.a1stream.domain.form.parts.SPQ030201Form;
import com.a1stream.parts.service.SPQ0302Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
 * @author liu chaoran
 */
@Component
public class SPQ0302Facade {

    @Resource
    private SPQ0302Service spq0302Service;

    @Resource
    private ValidateLogic validateLogic;

    public List<SPQ030201BO> findPartsAdjustmentHistoryList(SPQ030201Form model, String siteId) {

        //查询校验
        this.check(model);
        return spq0302Service.findPartsAdjustmentHistoryList(model, siteId);
    }

    private void check(SPQ030201Form form) {

        //检查起始时间和结束时间
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

    public List<SPQ030201BO> exportPartsAdjustmentHistory(SPQ030201Form model, String siteId) {

        List<SPQ030201BO> partsList = spq0302Service.findPartsAdjustmentHistoryList(model, siteId);

        BigDecimal totalInQty = BigDecimal.ZERO;
        BigDecimal totalInAmount = BigDecimal.ZERO;
        BigDecimal totalOutQty = BigDecimal.ZERO;
        BigDecimal totalOutAmount = BigDecimal.ZERO;
        // 对返回的部件编号进行失焦操作
        for (SPQ030201BO part : partsList) {
            String formattedPartNo = PartNoUtil.format(part.getPartsNo());
            part.setPartsNo(formattedPartNo);

            if (part.getAdjustmentDate() != null) {
                LocalDate date = LocalDate.parse(part.getAdjustmentDate(), DateTimeFormatter.BASIC_ISO_DATE);

                // 格式化日期对象为所需的字符串格式
                String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                // 将修改后的日期字符串设置回对象中
                part.setAdjustmentDate(formattedDate);
            }

            // 在每次循环内部对总量进行累加
            totalInQty = totalInQty.add(part.getInQty() != null ? part.getInQty() : BigDecimal.ZERO);
            totalInAmount = totalInAmount.add(part.getInAmount() != null ? part.getInAmount() : BigDecimal.ZERO);
            totalOutQty = totalOutQty.add(part.getOutQty() != null ? part.getOutQty() : BigDecimal.ZERO);
            totalOutAmount = totalOutAmount.add(part.getOutAmount() != null ? part.getOutAmount() : BigDecimal.ZERO);
        }

        // 创建底部合计行
        SPQ030201BO totalRow = new SPQ030201BO();
        totalRow.setLocation("Total:");

        // 将totalInQty和totalOutQty转换为BigDecimal类型，并设置到totalRow中
        totalRow.setInQty(totalInQty);
        totalRow.setInAmount(totalInAmount);
        totalRow.setOutQty(totalOutQty);
        totalRow.setOutAmount(totalOutAmount);

        // 将底部合计行添加到数据集
        partsList.add(totalRow);

        return partsList;
    }
}
