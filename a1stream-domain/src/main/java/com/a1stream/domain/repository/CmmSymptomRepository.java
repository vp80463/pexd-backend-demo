package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmSymptomRepositoryCustom;
import com.a1stream.domain.entity.CmmSymptom;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmSymptomRepository extends JpaExtensionRepository<CmmSymptom, Long>,CmmSymptomRepositoryCustom {

    List<CmmSymptom> findByProductSectionId(Long productSectionId);

    CmmSymptom findFirstBySymptomCd(String symptomCd);

    List<CmmSymptom> findBySiteIdAndProductSectionId(String siteId, Long productSectionId);
}
