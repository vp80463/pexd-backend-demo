package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ServicePaymentEditHistory;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServicePaymentEditHistoryRepository extends JpaExtensionRepository<ServicePaymentEditHistory, Long> {

    ServicePaymentEditHistory findByPaymentIdAndSiteIdAndPaymentStatus(Long paymentId, String siteId, String paymentStatus);
}
