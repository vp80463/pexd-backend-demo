package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmUnitPromotionItemRepositoryCustom;
import com.a1stream.domain.entity.CmmUnitPromotionItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmUnitPromotionItemRepository extends JpaExtensionRepository<CmmUnitPromotionItem, Long>, CmmUnitPromotionItemRepositoryCustom {

    List<CmmUnitPromotionItem> findByCmmSerializedProductIdIn(Set<Long> cmmSerializedProductIdSet);

    List<CmmUnitPromotionItem> findByFrameNoInAndPromotionListId(List<String> frameNos ,Long promotionListId);

    List<CmmUnitPromotionItem> findByPromotionListId(Long promotionListId);

    List<CmmUnitPromotionItem> findByPromotionListIdAndFacilityIdAndFrameNo(Long promotionListId, Long facilityId, String frameNo);

    CmmUnitPromotionItem findByUnitPromotionItemId(Long unitPromotionItemId);

    List<CmmUnitPromotionItem> findByUnitPromotionItemIdIn(List<Long> unitPromotionItemIds);

}
