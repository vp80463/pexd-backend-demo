package com.a1stream.domain.repository;

import com.a1stream.domain.custom.SalesOrderRepositoryCustom;
import com.a1stream.domain.entity.SalesOrder;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


@Repository
public interface SalesOrderRepository extends JpaExtensionRepository<SalesOrder, Long>, SalesOrderRepositoryCustom {

    List<SalesOrder> findBySiteIdInAndOrderNoIn(Set<String> siteIds, Set<String> orderNos);

    SalesOrder findBySalesOrderId(Long salesOrderId);

    List<SalesOrder> findBySalesOrderIdIn(Set<Long> salesOrderId);

    List<SalesOrder> findByOrderStatusAndSiteIdAndFacilityId(String orderStatus, String siteId, Long facilityId);

    List<SalesOrder> findBySiteIdAndOrderStatusIn(String siteId,List<String> orderStatusList);

    SalesOrder findBySiteIdAndOrderNo(String siteId, String salesOrderNo);
}
