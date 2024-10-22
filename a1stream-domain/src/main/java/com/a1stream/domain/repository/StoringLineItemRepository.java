package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.StoringLineItemRepositoryCustom;
import com.a1stream.domain.entity.StoringLineItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface StoringLineItemRepository extends JpaExtensionRepository<StoringLineItem, Long>, StoringLineItemRepositoryCustom {

    List<StoringLineItem> findByStoringLineId(Long storingLineId);

    List<StoringLineItem> findBySiteIdAndStoringLineIdIn(String siteId, Set<Long> storingLineIds);

    StoringLineItem findByStoringLineItemId(Long storingLineItemId);

    List<StoringLineItem> findByStoringLineItemIdIn(List<Long> storingLineItemIds);

    List<StoringLineItem> findByStoringLineItemIdIn(Set<Long> storingLineItemIdSet);

}
