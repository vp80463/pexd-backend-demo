package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ReceiptPoItemRelation;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ReceiptPoItemRelationRepository extends JpaExtensionRepository<ReceiptPoItemRelation, Long> {

    List<ReceiptPoItemRelation> findByReceiptSlipItemIdIn(List<Long> receiptSlipItemIds);

    ReceiptPoItemRelation findByReceiptSlipItemId(Long receiptSlipItemIds);

    List<ReceiptPoItemRelation> findByPurchaseOrderNoAndSiteId(String purchaseOrderNo, String siteId);

}
