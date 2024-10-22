package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ReceiptSlipItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ReceiptSlipItemRepository extends JpaExtensionRepository<ReceiptSlipItem, Long> {

    List<ReceiptSlipItem> findByReceiptSlipId(Long receiptSlipId);

    List<ReceiptSlipItem> findByReceiptSlipIdIn(Set<Long> receiptSlipIds);

    List<ReceiptSlipItem> findByReceiptSlipItemIdIn(List<Long> receiptSlipItemId);

    List<ReceiptSlipItem> findByReceiptSlipItemIdIn(Set<Long> receiptSlipItemId);

    ReceiptSlipItem findByReceiptSlipItemId(Long receiptSlipItemId);
}
