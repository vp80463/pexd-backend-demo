package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmUnitPromotionModel;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmUnitPromotionModelRepository extends JpaExtensionRepository<CmmUnitPromotionModel, Long> {

    List<CmmUnitPromotionModel> findByPromotionListId(Long promotionListId);

    List<CmmUnitPromotionModel> findByPromotionListIdAndProductIdIn(Long promotionListId, List<Long> modelIds);
}
