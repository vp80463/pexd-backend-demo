package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ProductStockStatusBackupRepositoryCustom;
import com.a1stream.domain.entity.ProductStockStatusBackup;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ProductStockStatusBackupRepository extends JpaExtensionRepository<ProductStockStatusBackup, Long>,ProductStockStatusBackupRepositoryCustom {

    @Query(value="select * from product_stock_status_backup "
               + "where site_id =:siteId "
               + "and facility_id =:facilityId "
               + "and product_classification  =:productClsType", nativeQuery=true)
    List<ProductStockStatusBackup> findBysitePoint(@Param("siteId") String siteId
                                                 , @Param("facilityId") Long facilityId
                                                 , @Param("productClsType") String productClsType);
}
