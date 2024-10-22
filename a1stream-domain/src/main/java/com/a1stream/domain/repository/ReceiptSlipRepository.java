package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ReceiptSlipRepositoryCustom;
import com.a1stream.domain.entity.ReceiptSlip;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ReceiptSlipRepository extends JpaExtensionRepository<ReceiptSlip, Long>, ReceiptSlipRepositoryCustom {

    ReceiptSlip findByReceiptSlipId(Long receiptSlipId);

    List<ReceiptSlip> findByReceiptSlipIdIn(Set<Long> receiptSlipId);
}
