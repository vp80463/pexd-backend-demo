package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ServiceOrderBatteryRepositoryCustom;
import com.a1stream.domain.entity.ServiceOrderBattery;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServiceOrderBatteryRepository extends JpaExtensionRepository<ServiceOrderBattery, Long>, ServiceOrderBatteryRepositoryCustom {

    List<ServiceOrderBattery> findByServiceOrderIdOrderByBatteryType(Long serviceOrderId);

    ServiceOrderBattery findFirstByServiceOrderId(Long serviceOrderId);
}
