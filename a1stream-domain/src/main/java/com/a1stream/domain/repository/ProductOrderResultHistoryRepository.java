package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ProductOrderResultHistory;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ProductOrderResultHistoryRepository extends JpaExtensionRepository<ProductOrderResultHistory, Long> {

    public List<ProductOrderResultHistory> findByProductIdInAndFacilityIdAndSiteId(List<Long> productId, Long facilityId, String siteId);

    public List<ProductOrderResultHistory> findByProductIdAndFacilityIdAndSiteId(Long productId, Long facilityId, String siteId);

    public List<ProductOrderResultHistory> findByFacilityIdAndSiteId(Long facilityId, String siteId);

    @Query(value="select * from product_order_result_history "
            + "where site_id =:siteId "
            + "and target_year in (:targetYear) "
            + "and product_id =:productId "
            + "and facility_id =:facilityId "
            , nativeQuery=true)
     List<ProductOrderResultHistory> findProductOrderResultHisRepositoryInYears( @Param("siteId") String siteId
                                                                               , @Param("targetYear") List<String> targetYear
                                                                               , @Param("productId") Long productId
                                                                               , @Param("facilityId") Long facilityId);

    @Query(value="select * from product_order_result_history "
            + "where site_id =:siteId "
            + "and target_year in (:targetYear) "
            + "and facility_id =:facilityId "
            , nativeQuery=true)
     List<ProductOrderResultHistory> findProductOrderResultHisRepositoryInYearsNoProductId( @Param("siteId") String siteId
                                                                                          , @Param("targetYear") List<String> targetYear
                                                                                          , @Param("facilityId") Long facilityId);
}
