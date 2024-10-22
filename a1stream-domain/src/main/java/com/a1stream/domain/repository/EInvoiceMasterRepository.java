package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.EInvoiceMaster;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface EInvoiceMasterRepository extends JpaExtensionRepository<EInvoiceMaster, Long> {

    EInvoiceMaster findBySiteId(String siteId);
}
