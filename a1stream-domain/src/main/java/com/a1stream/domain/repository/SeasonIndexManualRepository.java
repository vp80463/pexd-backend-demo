package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.SeasonIndexManual;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface SeasonIndexManualRepository extends JpaExtensionRepository<SeasonIndexManual, Long> {

    List<SeasonIndexManual> findBySiteIdAndProductCategoryIdInAndFacilityId(String siteId,List<Long> productCategoryIdList,Long facilityId);

    SeasonIndexManual findBySiteIdAndProductCategoryIdAndFacilityId(String siteId, Long productCategoryId, Long pointId);
}
