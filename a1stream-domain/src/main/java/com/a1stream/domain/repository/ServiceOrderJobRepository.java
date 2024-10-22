package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ServiceOrderJobRepositoryCustom;
import com.a1stream.domain.entity.ServiceOrderJob;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServiceOrderJobRepository extends JpaExtensionRepository<ServiceOrderJob, Long>, ServiceOrderJobRepositoryCustom{

    List<ServiceOrderJob> findByServiceOrderId(Long serviceOrderId);

    List<ServiceOrderJob> findByServiceOrderIdAndSiteIdAndSettleTypeId(Long orderId, String siteId, String type);
}
