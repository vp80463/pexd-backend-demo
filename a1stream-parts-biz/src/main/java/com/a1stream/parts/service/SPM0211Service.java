/**
 *
 */
package com.a1stream.parts.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.manager.DeliveryOrderManager;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.InvoiceManager;
import com.a1stream.common.manager.PickingInstructionManager;
import com.a1stream.common.manager.SalesOrderManager;
import com.a1stream.domain.bo.parts.SPM021101BO;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.DeliveryOrderRepository;
import com.a1stream.domain.repository.MstOrganizationRepository;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.MstOrganizationVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2215
*/
@Service
public class SPM0211Service {

    @Resource
    MstOrganizationRepository mstOrganizationRepository;

    @Resource
    DeliveryOrderRepository deliveryOrderRepository;

    @Resource
    DeliveryOrderItemRepository deliveryOrderItemRepository;

    @Resource
    private DeliveryOrderManager deliveryOrderManager;

    @Resource
    private SalesOrderManager salesOrderManager;

    @Resource
    private InventoryManager inventoryManager;

    @Resource
    private InvoiceManager invoiceManager;

    @Resource
    private PickingInstructionManager pickingInstructionManager;

    public Long getCustomerId(String siteId, String relationType) {

        return mstOrganizationRepository.getCustomerId(siteId, relationType);
    }

    public List<SPM021101BO> findRePurchaseAndRetail(String siteId, Long facilityId, String deliveryStatus, Long fromOrgnazationId, Long toOrgnazationId, String duNo){
        return BeanMapUtils.mapListTo(deliveryOrderRepository.findRePurchaseAndRetail(siteId,facilityId,deliveryStatus,fromOrgnazationId,toOrgnazationId,duNo), SPM021101BO.class);
    }

    public List<SPM021101BO> findReturnAndTransfer(String siteId, Long facilityId, String deliveryStatus, String inventoryTransactionType, String duNo){
        return BeanMapUtils.mapListTo(deliveryOrderRepository.findReturnAndTransfer(siteId,facilityId,deliveryStatus,inventoryTransactionType,duNo), SPM021101BO.class);
    }

    public List<DeliveryOrderItemVO> findDoiByDeliveryOrderIdIn(List<Long> doldToReports) {
        return BeanMapUtils.mapListTo(deliveryOrderItemRepository.findByDeliveryOrderIdIn(new HashSet<>(doldToReports)), DeliveryOrderItemVO.class);
    }

    public List<DeliveryOrderVO> findDoByDeliveryOrderIdIn(List<Long> doldToReports) {
        return BeanMapUtils.mapListTo(deliveryOrderRepository.findByDeliveryOrderIdIn(doldToReports), DeliveryOrderVO.class);
    }

    public DeliveryOrderVO findByDeliveryOrderId(Long id){
        return BeanMapUtils.mapTo(deliveryOrderRepository.findByDeliveryOrderId(id), DeliveryOrderVO.class);
    }


    public void doShipment(List<Long> doldToReports, List<DeliveryOrderVO> deliveryOrderList, List<DeliveryOrderItemVO> deliveryOrderItemList, Long personId, String personNm) {

        deliveryOrderManager.doShippingCompletion(doldToReports);
        pickingInstructionManager.doPickingCompletion(doldToReports);
        salesOrderManager.doShippingCompletion(deliveryOrderItemList);
        inventoryManager.doShippingCompletion(deliveryOrderItemList, PJConstants.InventoryTransactionType.TRANSFEROUT.getCodeDbid(), personId, personNm);
        deliveryOrderManager.doComplete(doldToReports);
    }

    public MstOrganizationVO findBySiteIdAndOrganizationCd(String dealerCode){

        // MstOrganization
        MstOrganizationVO mstOrgInfo = BeanMapUtils.mapTo(mstOrganizationRepository.findBySiteIdAndOrganizationCd(dealerCode, dealerCode), MstOrganizationVO.class);
        if (mstOrgInfo == null) {
            throw new BusinessCodedException("no MstOrganization exist: " + dealerCode);
        }

        return mstOrgInfo;
    }
}
