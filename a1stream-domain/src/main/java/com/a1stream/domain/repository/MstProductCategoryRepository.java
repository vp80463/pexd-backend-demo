package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.MstProductCategoryRepositoryCustom;
import com.a1stream.domain.entity.MstProductCategory;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface MstProductCategoryRepository extends JpaExtensionRepository<MstProductCategory, Long>,MstProductCategoryRepositoryCustom {

    List<MstProductCategory> findByCategoryTypeAndProductClassificationAndActiveFlagAndCategoryCdIn(String categoryType, String productClassification, String activeFlag, Set<String> categoryCdSet);

    List<MstProductCategory> findByCategoryTypeOrderByCategoryNm(String categoryType);

    List<MstProductCategory> findBySiteIdAndCategoryType(String siteId,String categoryType);

    MstProductCategory findByProductCategoryId(Long productCategoryId);

    List<MstProductCategory> findByProductClassificationAndSiteId(String productClassificationId, String siteId);

    MstProductCategory findBySiteIdAndProductCategoryIdAndCategoryType(String siteId,Long productCategoryId,String categoryType);

    List<MstProductCategory> findByCategoryCdIn(List<String> categoryCdList);

    List<MstProductCategory> findByCategoryCdInAndSiteId(Set<String> categoryCdList, String siteId);
}
