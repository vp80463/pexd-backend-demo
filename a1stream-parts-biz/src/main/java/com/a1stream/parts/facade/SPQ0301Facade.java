package com.a1stream.parts.facade;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPQ030101BO;
import com.a1stream.domain.bo.parts.SPQ030101PrintBO;
import com.a1stream.domain.form.parts.SPQ030101Form;
import com.a1stream.domain.vo.ReceiptPoItemRelationVO;
import com.a1stream.parts.service.SPQ0301Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
 *
* 功能描述:Parts Receive And Register Inquiry
*
* mid2287
* 2024年6月6日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/06   Wang Nan      New
 */
@Component
public class SPQ0301Facade {

    @Resource
    private SPQ0301Service spq0301Service;

    @Resource
    private HelperFacade helperFacade;

    public List<SPQ030101BO> getPartsReceiveList(SPQ030101Form form, String siteId) {
        this.check(form);
        return this.dataEdit(spq0301Service.getReceiptSlipList(form, siteId));
    }

    public List<SPQ030101BO> getPartsReceiveListDetail(@RequestBody final SPQ030101Form form, String siteId) {
        return spq0301Service.getPartsReceiveListDetail(form, siteId);
    }

    public List<SPQ030101BO> getDetailsByPurchaseOrderNo(SPQ030101Form form, String siteId) {
        List<SPQ030101BO> receiptSlipList = new ArrayList<>();
        List<ReceiptPoItemRelationVO> relationList = spq0301Service.findReceiptPoItemRelationVOList(form.getPurchaseOrderNo(), siteId);
        if (!relationList.isEmpty()) {
            List<Long> receiptSlipItemIdList = relationList.stream().map(ReceiptPoItemRelationVO::getReceiptSlipItemId).toList();
            receiptSlipList = this.dataEdit(spq0301Service.getReceiptSlipListByReceiptSlipItemId(receiptSlipItemIdList, siteId));
            return receiptSlipList;
        }
        return receiptSlipList;
    }

    private void check(SPQ030101Form form) {
        //检查ReceiptDate
        if (StringUtils.isNotBlank(form.getDateFrom()) && StringUtils.isNotBlank(form.getDateTo())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

            LocalDate dateFrom = LocalDate.parse(form.getDateFrom(), formatter);
            LocalDate dateTo = LocalDate.parse(form.getDateTo(), formatter);

            if (dateFrom.isAfter(dateTo)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00205", new String[] {
                                                    CodedMessageUtils.getMessage("label.toDate"),
                                                    CodedMessageUtils.getMessage("label.fromDate")}));
            }
        }

        //检查point
        if (StringUtils.isNotBlank(form.getPoint()) && Nulls.isNull(form.getPointId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.point"),
                                             form.getPoint(),
                                             CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        //检查supplier
        if (StringUtils.isNotBlank(form.getSupplier()) && Nulls.isNull(form.getSupplierId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.supplier"),
                                             form.getSupplier(),
                                             CodedMessageUtils.getMessage("label.supplier")}));
        }

        //检查parts
        if (StringUtils.isNotBlank(form.getParts()) && Nulls.isNull(form.getPartsId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.partsNo"),
                                             form.getParts(),
                                             CodedMessageUtils.getMessage("label.tableProduct")}));
        }
    }

    private List<SPQ030101BO> dataEdit(List<SPQ030101BO> list) {

        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(InventoryTransactionType.CODE_ID);

        for (SPQ030101BO bo : list) {

            bo.setTransactionType(codeMap.get(bo.getTransactionTypeCd()));

            if (StringUtils.isNotBlank(bo.getCompletedDate()) && StringUtils.isNotBlank(bo.getCompletedTime()) &&
                StringUtils.isNotBlank(bo.getInstructionDate()) && StringUtils.isNotBlank(bo.getInstructionTime())) {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD + CommonConstants.DB_DATE_FORMAT_H_M_S);

                LocalDateTime completedDateTime = LocalDateTime.parse(bo.getCompletedDate() + bo.getCompletedTime(), formatter);
                LocalDateTime instructionDateTime = LocalDateTime.parse(bo.getInstructionDate() + bo.getInstructionTime(), formatter);

                if (completedDateTime.isAfter(instructionDateTime)) {
                    //timeProcessing = storing_list.completed_date & completed_time - storing_list.instruction_date & storing_list.instruction_time
                    Duration duration = Duration.between(instructionDateTime, completedDateTime);
                    bo.setTimeProcessing(duration.toHours() + CommonConstants.CHAR_H +
                                        (duration.toMinutes() % 60)+ CommonConstants.CHAR_M);
                }
            }
        }
        return list;
    }

    public List<SPQ030101PrintBO> getPartsReceiveAndRegisterPrintList(SPQ030101Form form, String siteId) {
        List<SPQ030101BO> printList = new ArrayList<>();
        if(!CommonConstants.CHAR_N.equals(form.getFlag())) {
            printList = spq0301Service.getPartsReceiveAndRegisterPrintList(form, siteId);
        }else {
            printList = this.getDetailsByPurchaseOrderNo(form, siteId);
        }

        List<SPQ030101BO> printNewList = new ArrayList<>();
        printNewList=printList;
        if(form.getStoringLineId() !=null && !CommonConstants.CHAR_N.equals(form.getFlag())) {
            printList = printList.stream()
                    .filter(bo -> form.getStoringLineId().equals(bo.getStoringLineId()))
                    .collect(Collectors.toList());

            for(SPQ030101BO bo:printList) {

                if(form.getStoringLineId().equals(bo.getStoringLineId())) {
                    String formattedPartNo = PartNoUtil.format(bo.getPartsNo());
                    bo.setPartsNo(formattedPartNo);
                    bo.setPartsName(bo.getPartsName());
                    bo.setReceiptQty(bo.getReceiptQty());
                    bo.setBoQty(bo.getBoQty());
                    bo.setInstrQty(bo.getInstrQty());
                    bo.setLocationStockQty(bo.getLocationStockQty());
                    bo.setOrderNo(bo.getOrderNo());
                }
            }
            printNewList =printList;
        }

        List<SPQ030101PrintBO> returnList = new ArrayList<>();
        SPQ030101PrintBO printBO = new SPQ030101PrintBO();
        if (!printNewList.isEmpty()) {
            printBO.setDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMDHMS));
            printBO.setPointAbbr(form.getPoint().replaceFirst(CommonConstants.CHAR_SPACE, " / "));
            printBO.setDeliveryNo(printNewList.get(0).getSupplier());
            printBO.setReceiptNo(printNewList.get(0).getReceiptNo());
            printBO.setReceiptDate(LocalDate.parse(printNewList.get(0).getReceiptDate(), DateTimeFormatter.BASIC_ISO_DATE).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        }
        List<SPQ030101BO> detailList = printNewList;

        for(SPQ030101BO bo:detailList) {
            String formattedPartNo = PartNoUtil.format(bo.getPartsNo());
            bo.setPartsNo(formattedPartNo);
        }

        if(printList.isEmpty()) {
            SPQ030101BO bo = new SPQ030101BO();
            printBO.setDetailPrintList(Collections.singletonList(bo));
        }else {
            printBO.setDetailPrintList(detailList);
        }

        returnList.add(printBO);
        return returnList;
    }

}
