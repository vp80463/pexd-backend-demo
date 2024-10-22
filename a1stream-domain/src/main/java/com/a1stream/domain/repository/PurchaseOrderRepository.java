package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.PurchaseOrderRepositoryCustom;
import com.a1stream.domain.entity.PurchaseOrder;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface PurchaseOrderRepository extends JpaExtensionRepository<PurchaseOrder, Long>, PurchaseOrderRepositoryCustom {

    PurchaseOrder findByPurchaseOrderId(Long id);

    List<PurchaseOrder> findByPurchaseOrderIdIn(List<Long> purchaseOrderIds);

    List<PurchaseOrder> findBySiteIdAndOrderNoIn(String siteId, Set<String> orderNos);

    List<PurchaseOrder> findBySiteIdInAndOrderNoInAndFacilityIdIn(Set<String> siteIds, Set<String> orderNos, Set<Long> facilityIds);

    PurchaseOrder findBySiteIdAndFacilityIdAndOrderNo(String siteId, Long facilityId, String orderNo);

    List<PurchaseOrder> findBySiteIdAndOrderStatusIn(String siteId, List<String> orderStatusList);

    List<PurchaseOrder> findBySiteIdInAndOrderNoIn(List<String> siteIdList, List<String> orderNoList);

    List<PurchaseOrder> findBySiteIdInAndSalesOrderNoInAndSupplierWarehouseCdIn(Set<String> siteIdSet, Set<String> salesOrderNoSet, Set<String> supplierWarehouseCdSet);
}
