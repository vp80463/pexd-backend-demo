package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.SeasonIndexBatch;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;



@Repository
public interface SeasonIndexBatchRepository extends JpaExtensionRepository<SeasonIndexBatch, Long> {

    List<SeasonIndexBatch> findBySiteIdAndProductCategoryIdInAndFacilityId(String siteId,List<Long> productCategoryIdList,Long facilityId);

    SeasonIndexBatch findBySiteIdAndProductCategoryIdAndFacilityId(String siteId,Long productCategoryId,Long facilityId);
}
