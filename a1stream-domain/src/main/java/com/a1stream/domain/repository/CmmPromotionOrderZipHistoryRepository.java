package com.a1stream.domain.repository;

import com.a1stream.domain.entity.CmmPromotionOrderZipHistory;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CmmPromotionOrderZipHistoryRepository extends JpaExtensionRepository<CmmPromotionOrderZipHistory, Long> {

    CmmPromotionOrderZipHistory findByPromotionListIdAndZipNm(Long promotionListId, String zipNm);

    CmmPromotionOrderZipHistory findByPromotionOrderZipHistoryId(Long promotionOrderZipHistoryId);
}
