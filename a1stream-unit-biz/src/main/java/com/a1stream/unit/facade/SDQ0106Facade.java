package com.a1stream.unit.facade;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.common.constants.PJConstants.ReceiptSlipStatus;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.domain.bo.unit.SDQ010602BO;
import com.a1stream.domain.bo.unit.SDQ010602DetailBO;
import com.a1stream.domain.form.unit.SDQ010602Form;
import com.a1stream.unit.service.SDQ0106Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
* 功能描述: Fast Receipt Report (Detail)
*
* mid2287
* 2024年8月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/09/06   Wang Nan      New
*/
@Component
public class SDQ0106Facade {

    @Resource
    private SDQ0106Service sdq0106Service;

    @Resource
    private HelperFacade helperFacade;

    public SDQ010602BO getDetail(SDQ010602Form form) {

        SDQ010602BO bo = sdq0106Service.getDetail(form);
        Map<String, String> transactionTypeMap = helperFacade.getMstCodeInfoMap(InventoryTransactionType.CODE_ID);
        Map<String, String> receiptStatusMap = helperFacade.getMstCodeInfoMap(ReceiptSlipStatus.CODE_ID);
        bo.setTransactionType(transactionTypeMap.get(bo.getTransactionType()));
        bo.setReceiptStatus(receiptStatusMap.get(bo.getReceiptStatus()));

        List<SDQ010602DetailBO> list = this.getDetailList(form);

        if (!list.isEmpty()) {

            bo.setDetailList(list);
        }

        if (Nulls.isNull(bo) && list.isEmpty()) {

          throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                           CodedMessageUtils.getMessage("label.receiptSlipNo"),
                                           form.getReceiptSlipId().toString(),
                                           CodedMessageUtils.getMessage("label.tableReceiptSlipInfo")}));
        }

        return bo;
    }

    public List<SDQ010602DetailBO> getDetailList(SDQ010602Form form) {
        return sdq0106Service.getDetailList(form);
    }

}