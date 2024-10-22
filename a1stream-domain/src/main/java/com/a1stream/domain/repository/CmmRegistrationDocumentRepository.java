package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmRegistrationDocumentRepositoryCustom;
import com.a1stream.domain.entity.CmmRegistrationDocument;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmRegistrationDocumentRepository extends JpaExtensionRepository<CmmRegistrationDocument, Long>, CmmRegistrationDocumentRepositoryCustom {

    CmmRegistrationDocument findFirstBySerializedProductId(Long serializedProductId);

    List<CmmRegistrationDocument> findBySerializedProductIdIn(Set<Long> serializedProductIds);

    CmmRegistrationDocument findByBatteryIdAndSiteId(Long batteryId, String siteId);

    List<CmmRegistrationDocument> findByBatteryIdIn(Set<Long> batteryIds);
}
