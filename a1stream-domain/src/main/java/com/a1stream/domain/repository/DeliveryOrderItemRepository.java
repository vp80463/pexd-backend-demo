package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.DeliveryOrderItemRepositoryCustom;
import com.a1stream.domain.entity.DeliveryOrderItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface DeliveryOrderItemRepository extends JpaExtensionRepository<DeliveryOrderItem, Long>, DeliveryOrderItemRepositoryCustom {

    List<DeliveryOrderItem> findByDeliveryOrderId(Long deliveryOrderId);

    List<DeliveryOrderItem> findByDeliveryOrderIdIn(Set<Long> deliveryOrderIds);

    List<DeliveryOrderItem> findByDeliveryOrderItemIdIn(List<Long> deliveryOrderItemIds);

    List<DeliveryOrderItem> findBySalesOrderId(Long salesOrderId);

    DeliveryOrderItem findByDeliveryOrderItemId(Long deliveryOrderItemId);
}
