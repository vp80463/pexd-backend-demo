package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ProductStockStatusRepositoryCustom;
import com.a1stream.domain.entity.ProductStockStatus;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ProductStockStatusRepository extends JpaExtensionRepository<ProductStockStatus, Long>, ProductStockStatusRepositoryCustom {

    @Query(value="select * from product_stock_status "
            + "where site_id =:siteId "
            + "and facility_id =:facilityId "
            + "and product_id =:productId "
            + "and product_stock_status_type =:productStockStatusType", nativeQuery=true)
    ProductStockStatus findProductStockStatus( @Param("siteId") String siteId
                                            , @Param("facilityId") Long facilityId
                                            , @Param("productId") Long productId
                                            , @Param("productStockStatusType") String productStockStatusType);

    @Query(value="select * from product_stock_status "
            + "where site_id =:siteId "
            + "and facility_id =:facilityId "
            + "and product_id =:productId "
            + "and product_stock_status_type in :productStockStatusTypes", nativeQuery=true)
    List<ProductStockStatus> findProductStockStatusIn( @Param("siteId") String siteId
                                            , @Param("facilityId") Long facilityId
                                            , @Param("productId") Long productId
                                            , @Param("productStockStatusTypes") List<String> productStockStatusType);


    @Query(value="select * from product_stock_status "
            + "where site_id =:siteId "
            + "and facility_id =:facilityId "
            + "and product_id in :productIds "
            + "and product_classification  =:productClassification "
            + "and product_stock_status_type in :productStockStatusTypes", nativeQuery=true)
    List<ProductStockStatus> findStockStatusList( @Param("siteId") String siteId
                                            , @Param("facilityId") Long targetReceiptPointId
                                            , @Param("productIds") Set<Long> productIds
                                            , @Param("productClassification") String productClassification
                                            , @Param("productStockStatusTypes") Set<String> productStockStatusTypes);

    @Query(value="select * from product_stock_status "
            + "where site_id =:siteId "
            + "and product_id =:productId "
            + "and product_classification  =:productClassification "
            + "and product_stock_status_type in :productStockStatusTypes", nativeQuery=true)
    List<ProductStockStatus> findStockStatusListOneProduct( @Param("siteId") String siteId
                                            , @Param("productId") Long receiptProductId
                                            , @Param("productClassification") String productClassification
                                            , @Param("productStockStatusTypes") Set<String> productStockStatusTypes);

    List<ProductStockStatus> findBySiteIdAndFacilityIdAndProductStockStatusTypeInAndProductIdIn(String siteId, Long facilityId, List<String> productStockStatusTypeList, List<Long> productIdList);

    Integer countByFacilityIdAndSiteId(Long facilityId, String siteId);

    @Query(value="select * from product_stock_status "
            + "where site_id =:siteId "
            + "and facility_id =:facilityId "
            + "and product_classification  =:productClassification ", nativeQuery=true)
    List<ProductStockStatus> getPartStockStatus(@Param("siteId") String siteId
                                              , @Param("facilityId") Long facilityId
                                              , @Param("productClassification") String productClassification);

    List<ProductStockStatus> findBySiteIdAndFacilityIdAndProductIdIn(String siteId, Long facilityId, List<Long> productIdList);

    @Query(value="select * from product_stock_status "
            + "where site_id =:siteId "
            + "and facility_id =:facilityId "
            + "and product_stock_status_type =:productStockStatusTypes "
            + "and quantity <= 0 "
            + "and product_id in :productIds", nativeQuery=true)
    List<ProductStockStatus> getPartStockStatusQtyleZero(@Param("siteId")String siteId,
                                                         @Param("facilityId")Long facilityId,
                                                         @Param("productStockStatusTypes")String productStockStatusType,
                                                         @Param("productIds")List<Long> productIds);

    @Query(value="select * from product_stock_status "
            + "where site_id =:siteId "
            + "and facility_id =:facilityId "
            + "and product_id in :productIds "
            + "and product_classification  =:productClassification "
            + "and product_stock_status_type =:productStockStatusType", nativeQuery=true)
    List<ProductStockStatus> findStockStatusProductList( @Param("siteId") String siteId
                                                       , @Param("facilityId") Long facilityId
                                                       , @Param("productIds") Set<Long> productIds
                                                       , @Param("productClassification") String productClassification
                                                       , @Param("productStockStatusType") String productStockStatusType);

    ProductStockStatus findBySiteIdAndFacilityIdAndProductIdAndProductClassificationAndProductStockStatusType(String siteId, Long facilityId, Long productId, String productClassification, String productStockStatusType);

    List<ProductStockStatus> findBySiteIdAndFacilityIdAndProductIdAndProductClassificationAndProductStockStatusTypeIn(String siteId, Long fromFacilityId, Long productId, String productClassification, List<String> stockStatusTypeList);

    @Query(value="SELECT * FROM product_stock_status "
            + "WHERE site_id =:siteId "
            + "AND facility_id =:facilityId "
            + "AND product_id IN :productIdSet "
            + "AND product_stock_status_type =:productStockStatusType", nativeQuery=true)
    List<ProductStockStatus> findProductStockStatusList( @Param("siteId") String siteId
                                                       , @Param("facilityId") Long facilityId
                                                       , @Param("productIdSet") Set<Long> productIdSet
                                                       , @Param("productStockStatusType") String productStockStatusType);

    List<ProductStockStatus> findByFacilityIdAndProductIdInAndProductStockStatusType(Long facilityId, Set<Long> productIds, String productStockStatusType);
}
