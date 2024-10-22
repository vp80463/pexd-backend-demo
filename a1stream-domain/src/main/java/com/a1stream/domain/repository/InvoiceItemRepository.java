package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.InvoiceItemRepositoryCustom;
import com.a1stream.domain.entity.InvoiceItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface InvoiceItemRepository extends JpaExtensionRepository<InvoiceItem, Long>,InvoiceItemRepositoryCustom {

    List<InvoiceItem> findByInvoiceId(Long invoiceId);

    List<InvoiceItem> findBySalesOrderNo(String salesOrderNo);

    List<InvoiceItem> findByRelatedInvoiceItemIdIn(Set<Long> relatedInvoiceItemId);

}
