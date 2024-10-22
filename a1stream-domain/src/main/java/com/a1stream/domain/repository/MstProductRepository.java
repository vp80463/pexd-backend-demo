package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.MstProductRepositoryCustom;
import com.a1stream.domain.entity.MstProduct;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface MstProductRepository extends JpaExtensionRepository<MstProduct, Long>, MstProductRepositoryCustom {

    List<MstProduct> findByProductIdIn(Set<Long> productIds);

    List<MstProduct> findByProductCdInAndProductClassification(Set<String> partNoSet, String productClassification);

    MstProduct findByProductId(Long productId);

    List<MstProduct> findBySiteIdAndProductClassificationAndProductCdIn(String siteId,String productClassification,List<String> partsNoList);

    MstProduct findByProductCategoryId(Long middleGroupId);

    List<MstProduct> findByProductCdInAndSiteIdIn(List<String> productCd,List<String> siteId);

    List<MstProduct> findByProductCdIn(List<String> productCd);

    List<MstProduct> findByProductCdIn(Set<String> productCd);

    MstProduct findBySiteIdAndProductCd(String siteId, String productCd);

    MstProduct findFirstByProductCdAndSiteIdIn(String productCd,List<String> siteId);

    List<MstProduct> findBySiteIdAndProductCdIn(String siteId, List<String> productCd);

    boolean existsByProductCd(String productCd);

    @Query(value = "     SELECT * FROM mst_product     "
            + "      WHERE product_cd in (:productCds) "
            + "        AND site_id in (:siteIds)       "
            + "        AND product_classification = :productClassification ", nativeQuery=true)
    List<MstProduct> getProductByCds( @Param("productCds") List<String> productCds
                                    , @Param("siteIds") List<String> siteIds
                                    , @Param("productClassification") String productClassification);

    // List<CMM090101BO> findModelPriceList(CMM090101Form form);
}
