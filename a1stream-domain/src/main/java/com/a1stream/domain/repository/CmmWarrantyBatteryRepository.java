package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmWarrantyBattery;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmWarrantyBatteryRepository extends JpaExtensionRepository<CmmWarrantyBattery, Long> {

    CmmWarrantyBattery findFirstByBatteryId(Long batteryId);

    List<CmmWarrantyBattery> findByBatteryIdIn(Set<Long> batteryIds);
}
