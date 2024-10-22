package com.a1stream.domain.repository;

import com.a1stream.domain.entity.CmmMenuClickSituation;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author dong zhen
 */
@Repository
public interface CmmMenuClickSituationRepository extends JpaExtensionRepository<CmmMenuClickSituation, Long> {

    CmmMenuClickSituation findByUserIdAndMenuCdAndSiteIdAndCustomStatus(String userId, String menuCd, String siteId, String charN);

    List<CmmMenuClickSituation> findByUserIdAndSiteId(String userId, String baseSiteId);

    List<CmmMenuClickSituation> findByUserIdAndSiteIdAndCustomStatus(String userId, String baseSiteId, String customStatus);
}
