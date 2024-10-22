package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ReceiptSerializedItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ReceiptSerializedItemRepository extends JpaExtensionRepository<ReceiptSerializedItem, Long> {

    List<ReceiptSerializedItem> findBySerializedProductIdInAndSiteIdIn(Set<Long> serializedProductIdSet, Set<String> siteIdSet);

    List<ReceiptSerializedItem> findBySerializedProductIdInAndSiteId(Set<Long> serializedProductIdSet, String siteId);

    List<ReceiptSerializedItem> findByReceiptSlipIdIn(Set<Long> receiptSlipIds);
    
    List<ReceiptSerializedItem> findByReceiptSlipItemIdIn(List<Long> receiptSlipItemIds);

}
