/**
 *
 */
package com.a1stream.parts.facade;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.model.ConstantsBO;
import com.a1stream.domain.bo.parts.SPM021101BO;
import com.a1stream.domain.form.parts.SPM021101Form;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.parts.service.SPM0211Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
* 功能描述:
*
* @author mid2215
*/
@Slf4j
@Component
public class SPM0211Facade {

    @Resource
    private SPM0211Service spm0211Service;

    public List<SPM021101BO> findShipmentCompletionList(SPM021101Form form) {

        //如果输入point不存在，提示用户
        if(ObjectUtils.isEmpty(form.getPointCd())&& !ObjectUtils.isEmpty(form.getPointNm())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.point"), form.getPointNm(), CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        Long companyId = spm0211Service.findBySiteIdAndOrganizationCd(form.getSiteId()).getOrganizationId();
        Long customerId = spm0211Service.getCustomerId(form.getSiteId(), PJConstants.OrgRelationType.SUPPLIER.getCodeDbid());

        List<SPM021101BO> result = new ArrayList<>();

        // 取到回购和零售
        result.addAll(spm0211Service.findRePurchaseAndRetail(form.getSiteId(),
                                                             form.getPointId(),
                                                             MstCodeConstants.DeliveryStatus.ON_PICKING,
                                                             companyId,
                                                             customerId,
                                                             form.getDuNo()));
        // 取到退货和transfer
        result.addAll(spm0211Service.findReturnAndTransfer(form.getSiteId(),
                                                           form.getPointId(),
                                                           MstCodeConstants.DeliveryStatus.ON_PICKING,
                                                           PJConstants.InventoryTransactionType.SALESTOCKOUT.getCodeDbid(),
                                                           form.getDuNo()));

        // Transaction Type 转换
        List<ConstantsBO> iventoryTransactionTypeList = getAllConstants();
        Map<String,String> iventoryTransactionTypeMap = iventoryTransactionTypeList.stream().collect(Collectors.toMap(ConstantsBO::getCodeDbid, ConstantsBO::getCodeData1));

        for(SPM021101BO spm021101bo : result) {

            spm021101bo.setInventoryTransactionTypeNm(iventoryTransactionTypeMap.get(spm021101bo.getInventoryTransactionType()));
            spm021101bo.setCustomerNm(spm021101bo.getCustomerCd() + CommonConstants.CHAR_SPACE + spm021101bo.getCustomerNm());
        }

        return result;
    }

    public void confirmShipment(SPM021101Form form) {

        for( SPM021101BO spm021101bo : form.getContent()){
            if(!Objects.equals(spm021101bo.getUpdateCount(), spm0211Service.findByDeliveryOrderId( spm021101bo.getDeliveryOrderId()).getUpdateCount())){
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00314", new String[]{ CodedMessageUtils.getMessage("label.duNo"),spm021101bo.getDeliveryOrderNo(),CodedMessageUtils.getMessage("label.tableDeliveryOrder")}));
            }
        }

        // 拿到所有id
        List<Long> doldToReports = form.getContent().stream().map(SPM021101BO::getDeliveryOrderId).toList();

        // 拿到所有DeliveryOrderVO
        List<DeliveryOrderVO> deliveryOrderList = spm0211Service.findDoByDeliveryOrderIdIn(doldToReports);

        // 拿到所有DeliveryOrderItemVO
        List<DeliveryOrderItemVO> deliveryOrderItemList = spm0211Service.findDoiByDeliveryOrderIdIn(doldToReports);

        // 调用共通
        spm0211Service.doShipment(doldToReports, deliveryOrderList, deliveryOrderItemList, form.getPersonId(), form.getPersonNm());
    }

    /**
     * 取到所有 InventoryTransactionType
     */
    public static List<ConstantsBO> getAllConstants() {
        List<ConstantsBO> constantsList = new ArrayList<>();
        Field[] fields = PJConstants.InventoryTransactionType.class.getDeclaredFields();

        for (Field field : fields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getType().equals(ConstantsBO.class)) {
                try {
                    constantsList.add((ConstantsBO) field.get(null));
                } catch (IllegalAccessException e) {
                    log.error("Get All InventoryTransactionType error", e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        return constantsList;
    }

}
