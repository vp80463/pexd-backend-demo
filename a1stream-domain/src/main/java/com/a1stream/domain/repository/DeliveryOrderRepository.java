package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.DeliveryOrderRepositoryCustom;
import com.a1stream.domain.entity.DeliveryOrder;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface DeliveryOrderRepository extends JpaExtensionRepository<DeliveryOrder, Long>, DeliveryOrderRepositoryCustom{

    DeliveryOrder findByDeliveryOrderId(Long deliveryOrderId);

    DeliveryOrder findByDeliveryOrderNo(String deliveryOrderNo);

    List<DeliveryOrder> findByDeliveryOrderIdIn(List<Long> deliveryOrderIds);

    List<DeliveryOrder> findByDeliveryOrderIdInAndDeliveryStatus(List<Long> deliveryOrderIds, String deliverySts);

    DeliveryOrder findBySiteIdAndFromFacilityIdAndDeliveryOrderNo(String siteId, Long fromFacilityId, String deliveryOrderNo);

}
