package com.a1stream.parts.facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.PurchaseMethodType;
import com.a1stream.common.constants.PJConstants.PurchaseOrderPriorityType;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.domain.bo.parts.SPQ040101BO;
import com.a1stream.domain.form.parts.SPQ040101Form;
import com.a1stream.parts.service.SPQ0401Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Component
public class SPQ0401Facade {
    @Resource
    private SPQ0401Service spq0401Service;
    @Resource
    private HelperFacade helperFacade;

    public Page<SPQ040101BO> searchPurchaseOrderList(SPQ040101Form model, String siteId) {


        if(!ObjectUtils.isEmpty(model.getDateFrom()) && !ObjectUtils.isEmpty(model.getDateTo())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);
            LocalDate localDateFrom = LocalDate.parse(model.getDateFrom(),formatter);
            LocalDate localDateTo = LocalDate.parse(model.getDateTo(),formatter);
            if (localDateFrom.compareTo(localDateTo) > 0) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("error.dateEqAfter", new String[] {CodedMessageUtils.getMessage("label.orderDateTo"),CodedMessageUtils.getMessage("label.orderDate")}));
            }
        }

        if(ObjectUtils.isEmpty(model.getPartId()) && !ObjectUtils.isEmpty(model.getNewPart())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.partsNo"), model.getNewPart(), CodedMessageUtils.getMessage("label.productInformation")}));
        }

        if(ObjectUtils.isEmpty(model.getPointId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.point"), model.getPointvl(), CodedMessageUtils.getMessage("label.tablePartyFacility")}));
        }
        Page<SPQ040101BO> data = spq0401Service.searchPurchaseOrderList(model,siteId);

        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(PurchaseOrderPriorityType.CODE_ID, PurchaseMethodType.CODE_ID);

        data.getContent().forEach(item -> {
            item.setOrderType(codeMap.getOrDefault(item.getOrderType(),CommonConstants.CHAR_BLANK));
            item.setMethod(codeMap.getOrDefault(item.getMethod(), CommonConstants.CHAR_BLANK));
        });
        
        return data;
    }
}
