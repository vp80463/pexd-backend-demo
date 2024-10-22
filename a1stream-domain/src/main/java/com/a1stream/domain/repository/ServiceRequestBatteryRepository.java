package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ServiceRequestBatteryRepositoryCustom;
import com.a1stream.domain.entity.ServiceRequestBattery;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServiceRequestBatteryRepository extends JpaExtensionRepository<ServiceRequestBattery, Long>, ServiceRequestBatteryRepositoryCustom {

    List<ServiceRequestBattery> findByServiceRequestBatteryIdIn(List<Long> serviceRequestBatteryIdList);
}
