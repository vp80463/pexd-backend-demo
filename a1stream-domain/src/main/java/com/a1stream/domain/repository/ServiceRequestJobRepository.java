package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ServiceRequestJobRepositoryCustom;
import com.a1stream.domain.entity.ServiceRequestJob;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServiceRequestJobRepository extends JpaExtensionRepository<ServiceRequestJob, Long>, ServiceRequestJobRepositoryCustom {

    List<ServiceRequestJob> findByServiceRequestJobIdIn(List<Long> serviceRequestJobIdList);

    List<ServiceRequestJob> findByserviceRequestId(Long serviceRequestId);
}
