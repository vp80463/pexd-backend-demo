package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ReceiptManifestSerializedItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ReceiptManifestSerializedItemRepository extends JpaExtensionRepository<ReceiptManifestSerializedItem, Long> {

    List<ReceiptManifestSerializedItem> findBySerializedProductIdInAndSiteIdIn(Set<Long> serializedProductIdSet, Set<String> siteIdSet);

    List<ReceiptManifestSerializedItem> findBySerializedProductIdInAndSiteId(Set<Long> serializedProductIdSet, String siteId);

    List<ReceiptManifestSerializedItem> findByReceiptManifestItemIdIn(Set<Long> receiptManifestItemId);
}
