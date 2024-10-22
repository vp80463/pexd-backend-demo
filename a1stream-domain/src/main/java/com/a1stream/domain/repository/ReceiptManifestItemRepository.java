package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ReceiptManifestItemRepositoryCustom;
import com.a1stream.domain.entity.ReceiptManifestItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ReceiptManifestItemRepository extends JpaExtensionRepository<ReceiptManifestItem, Long>, ReceiptManifestItemRepositoryCustom {

    List<ReceiptManifestItem> findByReceiptManifestItemIdIn(List<Long> receiptManifestItemId);

    List<ReceiptManifestItem> findByCaseNoIn(List<String> caseNoList);

    ReceiptManifestItem findByReceiptManifestItemId(Long receiptManifestItemId);

    List<ReceiptManifestItem> findByReceiptManifestId(Long receiptManifestId);

    List<ReceiptManifestItem> findByReceiptManifestItemIdIn(Set<Long> receiptManifestItemIdSet);
   
    List<ReceiptManifestItem> findByReceiptManifestIdIn(Set<Long> receiptManifestId);
}
