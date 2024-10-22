package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ReorderGuidelineRepositoryCustom;
import com.a1stream.domain.entity.ReorderGuideline;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;



@Repository
public interface ReorderGuidelineRepository extends JpaExtensionRepository<ReorderGuideline, Long>,ReorderGuidelineRepositoryCustom {

    ReorderGuideline  findByReorderGuidelineId(Long reorderGuidelineId);

    List<ReorderGuideline>  findByReorderGuidelineIdIn(Set<Long> reorderGuidelineId);

    List<ReorderGuideline> findBySiteIdAndFacilityIdInAndProductId(String siteId, List<Long> facilityId, Long productId);

    List<ReorderGuideline> findByProductIdInAndSiteIdInAndFacilityId(List<Long> productId,List<String> sites,Long facilityId);

    List<ReorderGuideline> findBySiteIdAndFacilityIdAndProductIdIn(String siteId, Long facilityId, List<Long> productIdList);
}
