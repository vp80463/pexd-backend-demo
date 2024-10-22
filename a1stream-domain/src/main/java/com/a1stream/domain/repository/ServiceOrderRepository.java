package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ServiceOrderRepositoryCustom;
import com.a1stream.domain.entity.ServiceOrder;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServiceOrderRepository extends JpaExtensionRepository<ServiceOrder, Long>, ServiceOrderRepositoryCustom {

    ServiceOrder findFirstByOrderNoAndSiteId(String orderNo, String siteId);

    ServiceOrder findByServiceOrderId(Long serviceOrderId);

    ServiceOrder findByServiceOrderIdAndFacilityId(Long serviceOrderId, Long pointId);

    ServiceOrder findByFacilityIdAndSiteIdAndOrderNo(Long facilityId, String siteId, String orderNo);

    @Query("  FROM ServiceOrder  "
          + " WHERE orderNo = ?1 "
          + "   AND siteId = ?2 "
          + "   AND serviceCategoryId = ?3"
          + "   AND zeroKmFlag != ?4 "
          + "   AND facilityId =?5 " )
    ServiceOrder findBatteryClaimOrder(String orderNo, String siteId, String batteryClaimCtg, String zeroKmFlg, Long pointId);

    @Query(value="select * from service_order "
               + "where site_id =:siteId "
               + "and facility_id =:facilityId "
               + "and related_sales_order_id in (:salesOrderIds) ", nativeQuery=true)
    List<ServiceOrder> getByRelatedSalesOrderIds(@Param("siteId") String siteId
                                               , @Param("facilityId") Long facilityId
                                               , @Param("salesOrderIds") Set<Long> salesOrderIds);

    List<ServiceOrder> findBySiteIdAndZeroKmFlagAndOrderStatusIdIn(String siteId, String zeroKmFlag,List<String> orderStatusList);

    List<ServiceOrder> findBySiteIdAndZeroKmFlagAndOrderStatusIdInAndEvFlagAndServiceCategoryId(String siteId, String zeroKmFlag, List<String> orderStatus, String evFlag, String serviceCategoryId);

    List<ServiceOrder> findByFrameNoIn(List<String> frameNoList);
}
