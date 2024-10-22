package com.a1stream.parts.facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPQ020601BO;
import com.a1stream.domain.bo.parts.SPQ020601PrintBO;
import com.a1stream.domain.bo.parts.SPQ020602BO;
import com.a1stream.domain.form.parts.SPQ020601Form;
import com.a1stream.domain.form.parts.SPQ020602Form;
import com.a1stream.parts.service.SPQ0206Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: Picking Instruction Inquiry
*
* mid2287
* 2024年6月24日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/06   Wang Nan      New
*/
@Component
public class SPQ0206Facade {

    @Resource
    private SPQ0206Service spq0206Service;

    @Resource
    private HelperFacade helperFacade;

    public Page<SPQ020601BO> getPickingInstructionList(SPQ020601Form form) {

        //检索前check
        this.check(form);

        //一览画面检索
        Page<SPQ020601BO> list = spq0206Service.getPickingInstructionList(form);

        //数据库为codeDbid前台显示为codeData1
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(InventoryTransactionType.CODE_ID);
        for (SPQ020601BO bo : list) {
            bo.setTransactionType(codeMap.get(bo.getTransactionTypeCd()));
        }
        return list;
    }

    public List<SPQ020601BO> getPickingInstructionExportData(SPQ020601Form form) {
        return spq0206Service.getPickingInstructionExportData(form);
    }

    public List<SPQ020602BO> getPickingInstructionDetailList(SPQ020602Form form) {

        //详情页面检索
        List<SPQ020602BO> list = spq0206Service.getPickingInstructionDetailList(form);

        //数据库为codeDbid前台显示为codeData1
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(MstCodeConstants.DeliveryStatus.CODE_ID);
        for (SPQ020602BO bo : list) {
            bo.setDuStatus(codeMap.get(bo.getDuStatusCd()));
        }
        return list;
    }

    private void check(SPQ020601Form form) {

        //检查Instruction Date
        if (StringUtils.isNotBlank(form.getDateFrom()) && StringUtils.isNotBlank(form.getDateTo())) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

            LocalDate dateFrom = LocalDate.parse(form.getDateFrom(), formatter);
            LocalDate dateTo = LocalDate.parse(form.getDateTo(), formatter);

            if (dateFrom.isAfter(dateTo)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00205", new String[] {
                                                 CodedMessageUtils.getMessage("label.toDate"),
                                                 CodedMessageUtils.getMessage("label.fromDate")}));
            }
            long daysBetween = ChronoUnit.DAYS.between(dateFrom, dateTo);
            if (daysBetween > 180) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00339", new String[] {
                                                 CodedMessageUtils.getMessage("label.fromDate"),
                                                 CodedMessageUtils.getMessage("label.toDate")}));
            }
        }
    }

    public List<SPQ020601PrintBO> getPartsPickingListReport(SPQ020601Form form, String siteId) {
        List<SPQ020601BO> printList = spq0206Service.getPartsPickingListReport(siteId, form);

        SPQ020601PrintBO reportHeader = spq0206Service.getPartsPickingListReportHeader(siteId, form);

        for(SPQ020601BO bo: printList) {
            bo.setProductCd(PartNoUtil.format(bo.getProductCd()));
            bo.setBoxNo(CommonConstants.CHAR_BOXONE);
        }

        List<SPQ020601PrintBO> returnList = new ArrayList<>();
        SPQ020601PrintBO printBO = new SPQ020601PrintBO();
        printBO.setDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMDHMS));
        if(reportHeader!=null) {
            printBO.setPoint(reportHeader.getPoint());
            printBO.setPickingListNo(reportHeader.getPickingListNo());
            printBO.setBoxNo(reportHeader.getBoxNo());
        }
        if(printList.isEmpty()) {
            SPQ020601BO bo = new SPQ020601BO();
            printBO.setDetailPrintPickingList(Collections.singletonList(bo));
        }else {
            printBO.setDetailPrintPickingList(printList);
        }
        returnList.add(printBO);

        return returnList;
    }
}
