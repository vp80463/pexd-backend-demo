package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.QueueEinvoiceRepositoryCustom;
import com.a1stream.domain.entity.QueueEinvoice;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface QueueEinvoiceRepository extends JpaExtensionRepository<QueueEinvoice, Long>, QueueEinvoiceRepositoryCustom {

    List<QueueEinvoice> findBySiteIdAndRelatedInvoiceIdIn(String siteId, List<Long> relatedInvoiceId);

    List<QueueEinvoice> findBySiteIdAndDbIdIn(String siteId, List<Long> dbId);

}
