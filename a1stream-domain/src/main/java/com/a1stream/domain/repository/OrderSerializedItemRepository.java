package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.OrderSerializedItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface OrderSerializedItemRepository extends JpaExtensionRepository<OrderSerializedItem, Long> {

    List<OrderSerializedItem> findByOrderItemId(Long salesOrderItemId);

    List<OrderSerializedItem> findBySerializedProductIdIn(Set<Long> serializedProductIdSet);
}
