package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.PurchaseRecommendationRepositoryCustom;
import com.a1stream.domain.entity.PurchaseRecommendation;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface PurchaseRecommendationRepository extends JpaExtensionRepository<PurchaseRecommendation, Long>, PurchaseRecommendationRepositoryCustom {

    List<PurchaseRecommendation> findByPurchaseRecommendationIdIn(List<Long> recommendationIds);

    List<PurchaseRecommendation> findBySiteIdAndFacilityIdAndOrganizationId(String siteId, Long facilityId, Long supplierId);
}
