package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.PoSoItemRelationRepositoryCustom;
import com.a1stream.domain.entity.PoSoItemRelation;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface PoSoItemRelationRepository extends JpaExtensionRepository<PoSoItemRelation, Long>, PoSoItemRelationRepositoryCustom {

    public List<PoSoItemRelation> findBySalesOrderItemId(Long salesOrderItemId);

    @Query(value = "     SELECT sales_order_item_id FROM po_so_item_relation           "
                 + "      WHERE site_id = :siteId                                 "
                 + "        AND purchase_order_item_id = :purchaseOrderItemId     "
                 + "      LIMIT 1  ", nativeQuery=true)
    Long getSalesOrderItemIdByPurchaseOrderItemId(@Param("siteId") String siteId, @Param("purchaseOrderItemId") Long purchaseOrderItemId);

    @Query(value = "     SELECT sales_order_id FROM po_so_item_relation           "
                 + "      WHERE site_id = :siteId                                 "
                 + "        AND purchase_order_item_id = :purchaseOrderItemId     "
                 + "      LIMIT 1  ", nativeQuery=true)
    Long getSalesOrderIdByPurchaseOrderItemId(@Param("siteId") String siteId, @Param("purchaseOrderItemId") Long purchaseOrderItemId);
}
