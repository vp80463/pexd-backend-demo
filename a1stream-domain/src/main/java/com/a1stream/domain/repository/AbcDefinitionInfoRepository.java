package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.AbcDefinitionInfo;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface AbcDefinitionInfoRepository extends JpaExtensionRepository<AbcDefinitionInfo, Long> {

    List<AbcDefinitionInfo> findByProductCategoryIdAndSiteId(Long productCategoryId, String siteId);

    AbcDefinitionInfo findByAbcDefinitionInfoIdAndSiteId(long abcDefinitionInfoId, String siteId);

}
