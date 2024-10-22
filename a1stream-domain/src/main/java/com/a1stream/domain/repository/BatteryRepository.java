package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.Battery;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface BatteryRepository extends JpaExtensionRepository<Battery, Long> {

    Battery findFirstBySiteIdAndCmmBatteryInfoId(String siteId, Long cmmBatteryId);

    Battery findFirstByBatteryNoAndSiteId(String batteryNo, String siteId);

    Battery findBySiteIdAndSerializedProductId(String siteId, Long serializedProductId);

    List<Battery> findBySiteIdAndSerializedProductIdIn(String siteId, Set<Long> serializedProductIdSet);

    @Query(value="select * from battery "
            + "where serialized_product_id IN (:serializedProductId )"
            + "and from_date >= :sysDate "
            + "and to_date <= :sysDate ", nativeQuery=true)
    List<Battery> findBatteryVOList(@Param("serializedProductId") Set<Long> serializedProductIds,
                                    @Param("sysDate") String sysDate);

    List<Battery> findByBatteryNoIn(List<String> batteryNoList);

    List<Battery> findByBatteryNoIn(Set<String> batteryNos);

    Battery findBySerializedProductIdAndPositionSign(Long serializedProductId, String positionSign);

    Battery findByBatteryId(Long batteryId);

    List<Battery> findByBatteryIdIn(List<Long> batteryIdList);
}
