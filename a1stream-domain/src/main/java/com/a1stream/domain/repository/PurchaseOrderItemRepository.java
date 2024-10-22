package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.PurchaseOrderItemRepositoryCustom;
import com.a1stream.domain.entity.PurchaseOrderItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface PurchaseOrderItemRepository extends JpaExtensionRepository<PurchaseOrderItem, Long>, PurchaseOrderItemRepositoryCustom {

    List<PurchaseOrderItem> findByPurchaseOrderId(Long purchaseOrderId);

    List<PurchaseOrderItem> findByPurchaseOrderIdIn(List<Long> purchaseOrderIds);

    List<PurchaseOrderItem> findByPurchaseOrderIdInAndProductIdIn(Set<Long> purchaseOrderIds, Set<Long> productIds);

    PurchaseOrderItem findByPurchaseOrderIdAndProductId(Long purchaseOrderId, Long productId);

    @Modifying
    @Query("DELETE FROM PurchaseOrderItem e WHERE e.purchaseOrderItemId in :purchaseOrderItemIds")
    void deletePurchaseOrderItemByKey(@Param("purchaseOrderItemIds") List<Long> purchaseOrderItemIds);

    List<PurchaseOrderItem> findByPurchaseOrderItemIdIn(List<Long> purchaseOrderItemIds);

    PurchaseOrderItem findByPurchaseOrderItemId(Long purchaseOrderItemId);

    List<PurchaseOrderItem> findBySiteIdAndPurchaseOrderIdIn(String siteId, Set<Long> purchaseOrderIdSet);

    List<PurchaseOrderItem> findBySiteIdAndPurchaseOrderId(String siteId, Long purchaseOrderId);

    PurchaseOrderItem findBySiteIdAndPurchaseOrderIdAndProductId(String siteId, Long purchaseOrderId, Long productId);
}
