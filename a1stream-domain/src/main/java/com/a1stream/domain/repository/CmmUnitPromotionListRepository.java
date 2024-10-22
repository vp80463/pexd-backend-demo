package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmUnitPromotionListRepositoryCustom;
import com.a1stream.domain.entity.CmmUnitPromotionList;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmUnitPromotionListRepository extends JpaExtensionRepository<CmmUnitPromotionList, Long>, CmmUnitPromotionListRepositoryCustom {

    CmmUnitPromotionList findByPromotionListId(Long promotionListId);

    List<CmmUnitPromotionList> findByPromotionListIdIn(List<Long> promotionListIds);

    @Query(value="SELECT * FROM cmm_unit_promotion_list "
               + "ORDER BY date_created DESC "
               + "LIMIT 1 ", nativeQuery =true)
    CmmUnitPromotionList findCmmUnitPromotionList();
}
