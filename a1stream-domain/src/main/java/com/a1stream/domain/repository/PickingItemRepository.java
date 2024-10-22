package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.PickingItemRepositoryCustom;
import com.a1stream.domain.entity.PickingItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface PickingItemRepository extends JpaExtensionRepository<PickingItem, Long>, PickingItemRepositoryCustom {

    List<PickingItem> findByDeliveryOrderIdIn(List<Long> deliveryOrderIds);

    List<PickingItem> findByDeliveryOrderIdAndPickingListId(Long deliveryOrderId, Long pickingListId);

    List<PickingItem> findByPickingItemIdIn(List<Long> pickingItemIdList);

    List<PickingItem> findByDeliveryOrderItemIdIn(List<Long> deliveryOrderItemIdList);

    PickingItem findBySeqNoAndFacilityIdAndSiteId(String seqNo, Long facilityId, String siteId);
}
