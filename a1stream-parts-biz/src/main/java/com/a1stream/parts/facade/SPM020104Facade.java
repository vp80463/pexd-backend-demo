package com.a1stream.parts.facade;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.SalesOrderStatus;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.domain.bo.parts.SPM020104BO;
import com.a1stream.domain.form.parts.SPM020104Form;
import com.a1stream.domain.vo.SalesOrderVO;
import com.a1stream.parts.service.SPM020104Service;

import jakarta.annotation.Resource;
/**
* 功能描述:
*
* MID2303
* 2024年07月04日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/04   Ruan Hansheng     New
*/
@Component
public class SPM020104Facade {

    @Resource
    private SPM020104Service spm020104Service;

    @Resource
    private HelperFacade helperFacade;

    public SPM020104Form getPickingItemList(SPM020104Form form, PJUserDetails uc) {

        SalesOrderVO salesOrderVO = spm020104Service.getSalesOrderVO(form.getSalesOrderId());
        // 数据库为codeDbid 前台显示为codeData1
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(SalesOrderStatus.CODE_ID);
        form.setOrderNo(salesOrderVO.getOrderNo());
        form.setOrderType(codeMap.get(salesOrderVO.getOrderType()));
        form.setCustomer(salesOrderVO.getCustomerCd() + CommonConstants.CHAR_SPACE + salesOrderVO.getCustomerNm());
        form.setCustomerId(salesOrderVO.getCustomerId());
        form.setDeliveryPlanDate(salesOrderVO.getDeliveryPlanDate());
        form.setMemo(salesOrderVO.getComment());
        List<SPM020104BO> resultList = spm020104Service.getPickingItemList(form, uc);
        form.setAllTableDataList(resultList);
        return form;
    }

    public void shipment(SPM020104Form form, PJUserDetails uc) {
        
        spm020104Service.shipment(form, uc);
    }
}
