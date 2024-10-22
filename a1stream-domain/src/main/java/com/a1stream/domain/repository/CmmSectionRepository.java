package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmSectionRepositoryCustom;
import com.a1stream.domain.entity.CmmSection;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;



@Repository
public interface CmmSectionRepository extends JpaExtensionRepository<CmmSection, Long>, CmmSectionRepositoryCustom {

    CmmSection findByProductSectionIdAndSiteId(Long productSectionId, String siteId);
}
