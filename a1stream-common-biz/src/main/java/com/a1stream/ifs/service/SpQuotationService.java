package com.a1stream.ifs.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.domain.entity.PurchaseOrder;
import com.a1stream.domain.entity.PurchaseOrderItem;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstOrganizationRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.OrganizationRelationRepository;
import com.a1stream.domain.repository.PurchaseOrderItemRepository;
import com.a1stream.domain.repository.PurchaseOrderRepository;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstOrganizationVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.OrganizationRelationVO;
import com.a1stream.domain.vo.PurchaseOrderItemVO;
import com.a1stream.domain.vo.PurchaseOrderVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SpQuotationService {

    @Resource
    private OrganizationRelationRepository organizationRelationRepository;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private PurchaseOrderRepository purchaseOrderRepository;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private MstOrganizationRepository mstOrganizationRepository;

    @Resource
    private GenerateNoManager generateNoManager;

    @Resource
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    public List<OrganizationRelationVO> getOrganizationRelationVOList(Set<String> siteIdSet, String relationType) {

        return BeanMapUtils.mapListTo(organizationRelationRepository.findBySiteIdInAndRelationType(siteIdSet, relationType), OrganizationRelationVO.class);
    }

    public List<MstFacilityVO> getMstFacilityVOList(Set<String> siteIdSet, Set<String> consigneeSet, String shopFlag, String warehouseFlag) {

        return BeanMapUtils.mapListTo(mstFacilityRepository.getMstFacility(siteIdSet, consigneeSet, shopFlag, warehouseFlag), MstFacilityVO.class);
    }

    public List<PurchaseOrderVO> getPurchaseOrderVOList(Set<String> siteIdSet, Set<String> salesOrderNoSet, Set<String> supplierWarehouseCdSet) {

        return BeanMapUtils.mapListTo(purchaseOrderRepository.findBySiteIdInAndSalesOrderNoInAndSupplierWarehouseCdIn(siteIdSet, salesOrderNoSet, supplierWarehouseCdSet), PurchaseOrderVO.class);
    }

    public List<MstProductVO> getMstProductVOList(List<String> siteIdList, List<String> productCdList) {

        return BeanMapUtils.mapListTo(mstProductRepository.findByProductCdInAndSiteIdIn(productCdList, siteIdList), MstProductVO.class);
    }

    public List<MstOrganizationVO> getSupplierList(Set<String> siteIdSet) {

        return BeanMapUtils.mapListTo(mstOrganizationRepository.getPartSupplierList(siteIdSet), MstOrganizationVO.class);
    }

    public List<MstFacilityVO> getFacilityVOList(Set<String> siteIdSet, Set<String> facilityCdSet) {

        return BeanMapUtils.mapListTo(mstFacilityRepository.findBySiteIdInAndFacilityCdIn(siteIdSet, facilityCdSet), MstFacilityVO.class);
    }

    public String purchaseOrderNo(String siteId, Long pointId) {

        return generateNoManager.generateNonSerializedItemPurchaseOrderNo(siteId, pointId);
    }

    public void doQuotation(List<PurchaseOrderVO> purchaseOrderVOList, List<PurchaseOrderItemVO> purchaseOrderItemVOList) {

        purchaseOrderRepository.saveInBatch(BeanMapUtils.mapListTo(purchaseOrderVOList, PurchaseOrder.class));
        purchaseOrderItemRepository.saveInBatch(BeanMapUtils.mapListTo(purchaseOrderItemVOList, PurchaseOrderItem.class));
    }
}
