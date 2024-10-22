package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmBatteryRepositoryCustom;
import com.a1stream.domain.entity.CmmBattery;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmBatteryRepository extends JpaExtensionRepository<CmmBattery, Long>, CmmBatteryRepositoryCustom {

    List<CmmBattery> findByBatteryIdIn(List<Long> batteryIdList);

    List<CmmBattery> findByBatteryNoIn(List<String> batteryNoList);

    List<CmmBattery> findByBatteryNoIn(Set<String> batteryNos);

    CmmBattery findFirstByBatteryNo(String batteryNo);

    CmmBattery findByBatteryId(Long cmmBatteryInfoId);
}
