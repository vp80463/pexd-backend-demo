package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.bo.parts.SPM030901BO;
import com.a1stream.domain.custom.ProductStockTakingRepositoryCustom;
import com.a1stream.domain.entity.ProductStockTaking;
import com.a1stream.domain.form.parts.SPM030901Form;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ProductStockTakingRepository extends JpaExtensionRepository<ProductStockTaking, Long> ,ProductStockTakingRepositoryCustom {

    List<ProductStockTaking> findBySiteIdAndFacilityId(String siteId, Long facilityId);

    List<ProductStockTaking> findBySiteIdAndFacilityIdAndProductClassification(String siteId, Long facilityId, String productClassification);

    ProductStockTaking getByProductStockTakingId(Long productStockTakingId);

    @Override
    List<SPM030901BO> listProductStockTaking(SPM030901Form form);

    @Query(value =  " SELECT max(seq_no) AS seqNo      "
                 +  "   FROM product_stock_taking "
                 +  "  WHERE site_id      = ?1    "
                 +  "    AND facility_id  = ?2    "
                 , nativeQuery = true )
    public Integer getMaxSeqNo(String siteId, Long facilityId);

    List<ProductStockTaking> findByProductStockTakingIdIn(Set<Long> productStockTakingIds);

    ProductStockTaking findByProductIdAndLocationId(Long productId, Long locationId);
}
