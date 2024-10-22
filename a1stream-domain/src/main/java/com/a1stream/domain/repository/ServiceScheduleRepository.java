package com.a1stream.domain.repository;

import com.a1stream.domain.custom.ServiceScheduleRepositoryCustom;
import com.a1stream.domain.entity.ServiceSchedule;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ServiceScheduleRepository extends JpaExtensionRepository<ServiceSchedule, Long>, ServiceScheduleRepositoryCustom {

    List<ServiceSchedule> findBySiteIdAndScheduleDateInAndReservationStatusIn(String siteId, List<String> dateList, List<String> orderStatusList);

    ServiceSchedule findFirstBySiteIdAndFacilityIdAndScheduleDateAndPlateNoAndReservationStatus(String siteId, Long facilityId, String scheduleDate, String plateNo, String reservationStatus);

    ServiceSchedule findFirstByServiceOrderId(Long serviceOrderId);

    ServiceSchedule findByServiceScheduleId(Long serviceScheduleId);

    List<ServiceSchedule> findBySiteIdAndFacilityIdAndScheduleDateInAndReservationStatusIn(String siteId, Long pointId, List<String> dateList, List<String> orderStatusList);
}
