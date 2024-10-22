package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmSiteMasterRepositoryCustom;
import com.a1stream.domain.entity.CmmSiteMaster;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmSiteMasterRepository extends JpaExtensionRepository<CmmSiteMaster, String>,CmmSiteMasterRepositoryCustom {

    List<CmmSiteMaster> findBySiteIdIn(Set<String> dealerCdList);

    List<CmmSiteMaster> findBySiteIdInAndActiveFlag(Set<String> dealerCdList, String charY);

    List<CmmSiteMaster> findByActiveFlag(String charY);

    CmmSiteMaster findFirstBySiteId(String siteId);

    List<CmmSiteMaster> findBySiteCdInAndActiveFlag(Set<String> dealerCds, String charY);
}
