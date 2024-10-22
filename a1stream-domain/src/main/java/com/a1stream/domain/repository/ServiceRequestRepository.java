package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ServiceRequestRepositoryCustom;
import com.a1stream.domain.entity.ServiceRequest;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServiceRequestRepository extends JpaExtensionRepository<ServiceRequest, Long>, ServiceRequestRepositoryCustom {

    List<ServiceRequest> findByServiceRequestIdIn(List<Long> serviceRequestIdList);

    ServiceRequest findFirstBySiteIdAndRequestFacilityIdAndRequestNo(String siteId, Long pointId, String requestNo);

    List<ServiceRequest> findBySiteIdAndTargetMonth(String siteId, String targetMonth);
}
