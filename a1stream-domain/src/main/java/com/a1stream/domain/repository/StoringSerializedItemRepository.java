package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.StoringSerializedItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface StoringSerializedItemRepository extends JpaExtensionRepository<StoringSerializedItem, Long> {

    List<StoringSerializedItem> findBySiteIdInAndSerializedProductIdIn(Set<String> siteIdSet, Set<Long> serializedProductIdSet);

    List<StoringSerializedItem> findBySiteIdAndSerializedProductIdIn(String siteId, Set<Long> serializedProductIdSet);
}
