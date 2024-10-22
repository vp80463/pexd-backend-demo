package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.SalesOrderItemRepositoryCustom;
import com.a1stream.domain.entity.SalesOrderItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface SalesOrderItemRepository extends JpaExtensionRepository<SalesOrderItem, Long>, SalesOrderItemRepositoryCustom {

    List<SalesOrderItem> findBySalesOrderItemIdIn(Set<Long> salesOrderItemIds);

    List<SalesOrderItem> findBySalesOrderId(Long salesOrderId);

    List<SalesOrderItem> findBySalesOrderIdAndSiteId(Long salesOrderId, String siteId);

    SalesOrderItem findBySalesOrderItemId(Long salesOrderItemId);

    @Query(value="select * from sales_order_item "
               + "where site_id =:siteId "
               + "and sales_order_id in (:salesOrderIds) ", nativeQuery=true)
    List<SalesOrderItem> findBySalesOrderIds(@Param("siteId") String siteId
                                             , @Param("salesOrderIds") Set<Long> salesOrderIds);

    @Query(value="select * from sales_order_item "
            + "where site_id =:siteId "
            + "and seq_no = :seqNo "
            + "and sales_order_id = :salesOrderIds "
            + "order by date_created desc ", nativeQuery=true)
    List<SalesOrderItem> getLastSupersedingOrderItem(@Param("siteId") String siteId
                                                , @Param("seqNo") Integer seqNo
                                                , @Param("salesOrderIds") Long salesOrderIds);
}
