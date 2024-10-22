package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ProductInventoryRepositoryCustom;
import com.a1stream.domain.entity.ProductInventory;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ProductInventoryRepository extends JpaExtensionRepository<ProductInventory, Long>,ProductInventoryRepositoryCustom {

    @Query(value="select * from product_inventory "
            + "where site_id =:siteId "
            + "and facility_id =:facilityId "
            + "and product_id =:productId ", nativeQuery=true)
    List<ProductInventory> findProductInventorys( @Param("siteId") String siteId
                                            , @Param("facilityId") Long facilityId
                                            , @Param("productId") Long productId);

    List<ProductInventory> findByFacilityIdAndProductIdAndSiteId(Long facilityId, Long productId, String siteId);

    List<ProductInventory> findByFacilityIdInAndProductIdAndSiteId(List<Long> facilityIds, Long productId, String siteId);

    ProductInventory findByFacilityIdAndProductIdAndSiteIdAndLocationId(Long facilityId, Long productId, String siteId, Long locationId);

    List<ProductInventory> findByFacilityIdInAndProductIdAndSiteIdAndLocationIdIn(List<Long> facilityIds, Long productId, String siteId, List<Long> locationIds);

    List<ProductInventory> findByFacilityIdAndProductIdAndSiteIdAndPrimaryFlag(Long facilityId, Long productId, String siteId, String y);

    List<ProductInventory> findByFacilityIdInAndProductIdAndSiteIdAndPrimaryFlag(List<Long> facilityIds, Long productId, String siteId, String y);

    ProductInventory findFirstByFacilityIdAndProductIdAndSiteIdAndPrimaryFlag(Long facilityId, Long productId, String siteId, String y);

    ProductInventory findFirstByFacilityIdAndProductIdAndSiteIdAndLocationId(Long facilityId, Long productId, String siteId, Long locationId);

    @Query(value="select * from product_inventory "
               + "where site_id =:siteId "
               + "and product_id in:productIds "
               + "and facility_id =:facilityId "
               + "and location_id in:locationIds", nativeQuery=true)
    List<ProductInventory> findProductInventoryList( @Param("siteId") String siteId
                                                   , @Param("productIds") Set<Long> productIds
                                                   , @Param("facilityId") Long facilityId
                                                   , @Param("locationIds") Set<Long> locationIds);

    @Query(value = "select * from product_inventory "
                    + "where site_id =:siteId "
                    + "and product_id =:productId "
                    + "and facility_id =:facilityId "
                    + "and location_id  =:locationId", nativeQuery = true)
    ProductInventory findProductInventoryByLocationId(@Param("siteId") String siteId
            , @Param("productId") Long productId
            , @Param("facilityId") Long facilityId
            , @Param("locationId") Long locationIds);

    ProductInventory findByProductInventoryId(Long productInventoryId);

    List<ProductInventory> findBySiteIdAndProductIdAndFacilityIdAndLocationId(String siteId, Long productId ,Long facilityId ,Long locationId);

    List<ProductInventory> findBySiteIdAndProductIdAndFacilityIdAndLocationIdIn(String siteId, Long productId ,Long facilityId ,List<Long> locationIds);

    List<ProductInventory> findByProductInventoryIdIn(Set<Long> productInventoryIds);

    List<ProductInventory> findBySiteIdAndFacilityIdAndLocationId(String siteId, Long facilityId, Long locationId);

    List<ProductInventory> findBySiteIdAndFacilityIdAndProductIdAndLocationIdNot(String siteId, Long facilityId, Long productId, Long locationId);

    List<ProductInventory> findBySiteIdAndFacilityIdAndProductIdIn(String siteId, Long facilityId, List<Long> productIdList);

    List<ProductInventory> findBySiteIdAndFacilityIdAndLocationIdInAndPrimaryFlag(String siteId, Long facilityId, Set<Long> locationIds , String flag);

    List<ProductInventory> findByLocationIdInAndPrimaryFlag(Set<Long> locationIds , String flag);

    List<ProductInventory> findByLocationIdAndPrimaryFlag(Long locationId , String flag);

    @Query(value="select * from product_inventory "
            + "where site_id =:siteId "
            + "and product_id in:productIds "
            + "and facility_id =:facilityId "
            + "and primary_flag = :flag ", nativeQuery=true)
    List<ProductInventory> findMainProductInventoryList( @Param("siteId") String siteId
                                                   , @Param("productIds") Set<Long> productIds
                                                   , @Param("facilityId") Long facilityId
                                                   , @Param("flag") String flag );

    Integer countBySiteIdAndFacilityId(String siteId, Long facilityId);

    @Query(value="select * from product_inventory "
               + "where site_id =:siteId          "
               + "and facility_id =:facilityId    "
               + "and product_classification =:productClsType ", nativeQuery=true)
    List<ProductInventory> getPartBysitePoint( @Param("siteId") String siteId
                                            ,  @Param("facilityId") Long facilityId
                                            ,  @Param("productClsType") String productClsType);
}
