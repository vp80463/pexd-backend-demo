package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ProductOrderResultSummaryRepositoryCustom;
import com.a1stream.domain.entity.ProductOrderResultSummary;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ProductOrderResultSummaryRepository extends JpaExtensionRepository<ProductOrderResultSummary, Long>,ProductOrderResultSummaryRepositoryCustom {

    @Query(value="select * from product_order_result_summary "
            + "where site_id =:siteId "
            + "and target_year =:targetYear "
            + "and product_id =:productId "
            + "and facility_id =:facilityId "
            + "limit 1" , nativeQuery=true)
     ProductOrderResultSummary findProductOrderResultSummery( @Param("siteId") String siteId
                                            , @Param("targetYear") String targetYear
                                            , @Param("productId") Long productId
                                            , @Param("facilityId") Long facilityId);


    @Query(value="select * from product_order_result_summary "
            + "where site_id =:siteId "
            + "and target_year in (:targetYear) "
            + "and product_id in (:productId) "
            + "and facility_id =:facilityId "
            , nativeQuery=true)
     List<ProductOrderResultSummary> findProductOrderResultSummeryInYears( @Param("siteId") String siteId
                                            , @Param("targetYear") List<String> targetYear
                                            , @Param("productId") List<Long> productId
                                            , @Param("facilityId") Long facilityId);

    @Query(value="select * from product_order_result_summary "
            + "where site_id =:siteId "
            + "and target_year in (:targetYear) "
            + "and facility_id =:facilityId "
            , nativeQuery=true)
     List<ProductOrderResultSummary> findProductOrderResultSummeryInYearsNoProductId( @Param("siteId") String siteId
                                            , @Param("targetYear") List<String> targetYear
                                            , @Param("facilityId") Long facilityId);

    List<ProductOrderResultSummary> findBySiteIdAndProductIdAndFacilityId(String siteId, Long productId, Long facilityId);

    List<ProductOrderResultSummary> findBySiteIdAndFacilityId(String siteId, Long facilityId);

    List<ProductOrderResultSummary> findBySiteIdAndProductIdInAndFacilityId(String siteId,List<Long> productId, Long facilityId);

    @Query(value="select * from product_order_result_summary "
            + "where site_id =:siteId "
            + "and target_year in (:targetYear) "
            + "and product_id =:productId "
            + "and facility_id =:facilityId "
            , nativeQuery=true)
     List<ProductOrderResultSummary> findProductOrderResultSummeryInYears( @Param("siteId") String siteId
                                            , @Param("targetYear") List<String> targetYear
                                            , @Param("productId") Long productId
                                            , @Param("facilityId") Long facilityId);

}
