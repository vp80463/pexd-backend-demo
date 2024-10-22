package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ProductCost;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ProductCostRepository extends JpaExtensionRepository<ProductCost, Long> {

    List<ProductCost> findByProductIdInAndCostTypeAndSiteId(Set<Long> productIds, String costType, String siteId);

    ProductCost findByProductIdAndCostTypeAndSiteId(Long productId, String costType, String siteId);

    @Query(value="select * from product_cost "
            + "where site_id =:siteId "
            + "and product_id in :productIds "
            + "and product_classification  =:productClassification "
            + "and cost_type = :costType", nativeQuery=true)
    List<ProductCost> findProductCostVOList(@Param("siteId") String siteId
                          , @Param("productIds") Set<Long> productIds
                          , @Param("productClassification") String productClassification
                          , @Param("costType")  String costType);

    List<ProductCost> findByProductIdInAndCostTypeAndSiteIdIn(List<Long> productIdList, String costType, List<String> siteIdList);

    ProductCost findBySiteIdAndProductIdAndCostTypeAndProductClassification(String siteId, Long productId, String receiveCost, String codeDbid);

    ProductCost findBySiteIdAndProductIdAndCostType(String siteId, Long productId, String receiveCostType);

    List<ProductCost> findByProductIdInAndCostType(Set<Long> productIdList, String costType);
}
