package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.DeliverySerializedItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface DeliverySerializedItemRepository extends JpaExtensionRepository<DeliverySerializedItem, Long> {

}
