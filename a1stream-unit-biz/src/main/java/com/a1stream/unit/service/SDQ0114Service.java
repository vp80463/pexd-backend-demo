package com.a1stream.unit.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.unit.SDQ011401BO;
import com.a1stream.domain.bo.unit.SDQ011402BO;
import com.a1stream.domain.form.unit.SDQ011401Form;
import com.a1stream.domain.form.unit.SDQ011402Form;
import com.a1stream.domain.repository.CmmConsumerRepository;
import com.a1stream.domain.repository.CmmMstOrganizationRepository;
import com.a1stream.domain.repository.DeliveryOrderRepository;
import com.a1stream.domain.repository.InventoryTransactionRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.SerializedProductTranRepository;
import com.a1stream.domain.vo.CmmConsumerVO;
import com.a1stream.domain.vo.CmmMstOrganizationVO;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: Stock In Out History Inquiry
*
* mid2287
* 2024年7月25日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/07/25   Wang Nan      New
*/
@Service
public class SDQ0114Service {

    @Resource
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private DeliveryOrderRepository deliveryOrderRepository;

    @Resource
    private CmmMstOrganizationRepository cmmMstOrganizationRepository;

    @Resource
    private CmmConsumerRepository cmmConsumerRepository;

    @Resource
    private SerializedProductTranRepository serializedProductTranRepository;

    public Page<SDQ011401BO> getStockInOutHistoryList(SDQ011401Form form) {
        return inventoryTransactionRepository.getStockInOutHistoryList(form);
    }

    public List<MstFacilityVO> getMstFacilityVOList(Set<Long> facilityIds) {
        return BeanMapUtils.mapListTo(mstFacilityRepository.findByFacilityIdIn(facilityIds), MstFacilityVO.class);
    }

    public List<DeliveryOrderVO> getDeliveryOrderVOList(List<Long> deliveryOrderIds) {
        return BeanMapUtils.mapListTo(deliveryOrderRepository.findByDeliveryOrderIdIn(deliveryOrderIds), DeliveryOrderVO.class);
    }

    public List<CmmConsumerVO> getCmmConsumerVOList(List<Long> consumerIds) {
        return BeanMapUtils.mapListTo(cmmConsumerRepository.findByConsumerIdIn(consumerIds), CmmConsumerVO.class);
    }

    public List<CmmMstOrganizationVO> getCmmMstOrganizationVOList(List<Long> organizationIds) {
        return BeanMapUtils.mapListTo(cmmMstOrganizationRepository.findByOrganizationIdIn(organizationIds), CmmMstOrganizationVO.class);
    }

    public List<SDQ011402BO> getStockHistoryDetail(SDQ011402Form form) {
        return serializedProductTranRepository.getStockHistoryDetail(form);
    }

}
