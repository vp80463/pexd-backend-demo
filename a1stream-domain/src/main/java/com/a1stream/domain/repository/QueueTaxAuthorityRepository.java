package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.QueueTaxAuthorityRepositoryCustom;
import com.a1stream.domain.entity.QueueTaxAuthority;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface QueueTaxAuthorityRepository extends JpaExtensionRepository<QueueTaxAuthority, Long>, QueueTaxAuthorityRepositoryCustom {

    List<QueueTaxAuthority> findBySiteIdAndRelatedInvoiceIdIn(String siteId, List<Long> relatedInvoiceId);

}
