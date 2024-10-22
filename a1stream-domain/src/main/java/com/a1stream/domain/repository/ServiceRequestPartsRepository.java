package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ServiceRequestPartsRepositoryCustom;
import com.a1stream.domain.entity.ServiceRequestParts;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServiceRequestPartsRepository extends JpaExtensionRepository<ServiceRequestParts, Long>, ServiceRequestPartsRepositoryCustom {

    List<ServiceRequestParts> findByServiceRequestPartsIdIn(List<Long> serviceRequestPartsIdList);

    List<ServiceRequestParts> findByserviceRequestId(Long serviceRequestId);
}
