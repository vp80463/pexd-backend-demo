/**
 *
 */
package com.a1stream.parts.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.bo.SalesReturnBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.InvoiceType;
import com.a1stream.common.manager.DeliveryOrderManager;
import com.a1stream.common.manager.InventoryManager;
import com.a1stream.common.manager.InvoiceManager;
import com.a1stream.common.manager.ReceiptSlipManager;
import com.a1stream.common.manager.SalesOrderManager;
import com.a1stream.common.manager.StoringManager;
import com.a1stream.domain.bo.parts.SPM020201PrintBO;
import com.a1stream.domain.bo.parts.SPM020201PrintDetailBO;
import com.a1stream.domain.bo.parts.SPM020202BO;
import com.a1stream.domain.entity.DeliveryOrder;
import com.a1stream.domain.entity.DeliveryOrderItem;
import com.a1stream.domain.entity.StoringLineItem;
import com.a1stream.domain.form.parts.SPM020201Form;
import com.a1stream.domain.repository.DeliveryOrderItemRepository;
import com.a1stream.domain.repository.DeliveryOrderRepository;
import com.a1stream.domain.repository.InvoiceItemRepository;
import com.a1stream.domain.repository.InvoiceRepository;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.MstOrganizationRepository;
import com.a1stream.domain.repository.OrganizationRelationRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.StoringLineItemRepository;
import com.a1stream.domain.repository.StoringLineRepository;
import com.a1stream.domain.vo.DeliveryOrderItemVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.InvoiceItemVO;
import com.a1stream.domain.vo.InvoiceVO;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.MstOrganizationVO;
import com.a1stream.domain.vo.OrganizationRelationVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.ReceiptSlipVO;
import com.a1stream.domain.vo.StoringLineItemVO;
import com.a1stream.domain.vo.StoringLineVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

/**
* 功能描述:
*
* @author mid2259
*/
@Service
public class SPM0202Service {

    @Resource
    InvoiceRepository invoiceRepository;

    @Resource
    InvoiceItemRepository invoiceItemRepository;

    @Resource
    MstOrganizationRepository mstOrganizationRepository;

    @Resource
    ProductInventoryRepository productInventoryRepository;

    @Resource
    LocationRepository locationRepository;

    @Resource
    InventoryManager inventoryManager;

    @Resource
    InvoiceManager invoiceManager;

    @Resource
    ReceiptSlipManager receiptSlipManager;

    @Resource
    DeliveryOrderManager deliveryOrderManager;

    @Resource
    DeliveryOrderRepository deliveryOrderRepository;

    @Resource
    DeliveryOrderItemRepository deliveryOrderItemRepository;

    @Resource
    SalesOrderManager salesOrderManager;

    @Resource
    StoringManager storingManager;

    @Resource
    StoringLineItemRepository storingLineItemRepository;

    @Resource
    StoringLineRepository storingLineRepository;

    @Resource
    OrganizationRelationRepository organizationRelationRepository;

    public List<OrganizationRelationVO> findBySiteIdAndRelationType(String siteId, String relationType) {
        
        return BeanMapUtils.mapListTo(organizationRelationRepository.findBySiteIdAndRelationType(siteId, relationType), OrganizationRelationVO.class);
    }

    public InvoiceVO searchInvoiceByInvoiceNo(SPM020201Form model) {
        return BeanMapUtils.mapTo(invoiceRepository.searchInvoiceInfoByInvoiceNo(model.getSiteId(), model.getPointId(), InvoiceType.SALES_INVOICE.getCodeDbid(), model.getInvoiceNo()), InvoiceVO.class);
    }

    public MstOrganizationVO searchOrganizationById(Long id) {
        return BeanMapUtils.mapTo(mstOrganizationRepository.findFirstByOrganizationId(id), MstOrganizationVO.class);
    }

    public List<InvoiceItemVO> searchInvoiceItemByInvoiceId(Long invoiceId) {
        return BeanMapUtils.mapListTo(invoiceItemRepository.findByInvoiceId(invoiceId), InvoiceItemVO.class);
    }

    public List<ProductInventoryVO> findMainProductInventoryList(SPM020201Form model, Set<Long> productIds) {
        return BeanMapUtils.mapListTo(productInventoryRepository.findMainProductInventoryList(model.getSiteId(), productIds, model.getPointId(), CommonConstants.CHAR_Y), ProductInventoryVO.class);
    }

    public List<LocationVO> findByLocationIdIn(Set<Long> locationIds,Long pointId) {
        return BeanMapUtils.mapListTo(locationRepository.findByLocationIdInAndFacilityId(locationIds,pointId), LocationVO.class);
    }

    public List<StoringLineItemVO> findStoringLineItemByStoringLineId(Long id){
        return BeanMapUtils.mapListTo(storingLineItemRepository.findByStoringLineId(id), StoringLineItemVO.class);
    }

    public List<InvoiceItemVO> searchReturnInvoiceItem(Set<Long> invoiceItemId) {
        return BeanMapUtils.mapListTo(invoiceItemRepository.findByRelatedInvoiceItemIdIn(invoiceItemId), InvoiceItemVO.class);
    }

    public SPM020201Form confirmSalesReturn(SPM020201Form model,SalesReturnBO salesReturnBO,DeliveryOrderVO deliveryOrderVO,List<DeliveryOrderItemVO> deliveryOrderItemVOs, Long personId, String personNm){

        deliveryOrderRepository.save(BeanMapUtils.mapTo(deliveryOrderVO, DeliveryOrder.class));
        deliveryOrderItemRepository.saveInBatch(BeanMapUtils.mapListTo(deliveryOrderItemVOs, DeliveryOrderItem.class));

        InvoiceVO invoiceVO = invoiceManager.doSalesReturn(salesReturnBO, model.getSiteId());

        model.setReturnInvoiceNo(invoiceVO.getInvoiceNo());
        model.setReturnInvoiceId(invoiceVO.getInvoiceId());

        ReceiptSlipVO receiptSlipVO = receiptSlipManager.doSalesReturn(deliveryOrderVO);

        if (!ObjectUtils.isEmpty(receiptSlipVO)) {
            inventoryManager.doSalesReturn(receiptSlipVO, personId, personNm);

            List<ReceiptSlipVO> receiptSlips = new ArrayList<>();

            receiptSlips.add(receiptSlipVO);
            List<Long> storingListIds = storingManager.doSalesReturn(model.getPointId(), receiptSlips);

            List<StoringLineVO> storingLineVOs= BeanMapUtils.mapListTo(storingLineRepository.findByStoringListId(storingListIds.get(0)), StoringLineVO.class);

            // key : storingLineId, value : true(It's processed) null(It's not processed)
            Map<Long, String> processedFlagMapping = new HashMap<>();

            List<StoringLineItemVO> itemResult = new ArrayList<>();
            for (SPM020202BO bo : model.getContent()) {
                Long partId = bo.getPartsId();
                salesOrderManager.updateProductOrderResultSummary(model.getInvoiceDate(), model.getPointId(), bo.getPartsId(), model.getSiteId(), bo.getReturnQty());

                for(StoringLineVO storingLineInfo :storingLineVOs){

                    if (!StringUtils.equals(processedFlagMapping.get(storingLineInfo.getStoringLineId()), CommonConstants.CHAR_ONE) && partId.equals(storingLineInfo.getProductId())) {
                            StoringLineItemVO storingLineItemVO = this.findStoringLineItemByStoringLineId(storingLineInfo.getStoringLineId()).get(CommonConstants.INTEGER_ZERO);
                            storingLineItemVO.setLocationId(bo.getLocationId());
                            storingLineItemVO.setStoredQty(storingLineInfo.getInstuctionQty());
                            storingLineInfo.setStoredQty(storingLineInfo.getInstuctionQty());
                            itemResult.add(storingLineItemVO);
                            processedFlagMapping.put(storingLineInfo.getStoringLineId(), CommonConstants.CHAR_ONE);
                    }

                }
            }
            storingLineItemRepository.saveInBatch(BeanMapUtils.mapListTo(itemResult, StoringLineItem.class));

            for (StoringLineVO storingLineVO : storingLineVOs) {
                storingManager.doStoringReport(storingLineVO);
            }

            inventoryManager.doStoringReport(storingLineVOs);
        }

        return model;

    }

    public SPM020201PrintBO getPartsSalesReturnInvoiceForFinanceData(Long invoiceId) {
        return invoiceRepository.getPartsSalesReturnInvoiceForFinanceData(invoiceId);
     }

    public List<SPM020201PrintDetailBO> getPartsSalesReturnInvoiceForFinanceDetailList(Long invoiceId) {
        return invoiceRepository.getPartsSalesReturnInvoiceForFinanceDetailList(invoiceId);
     }
}
