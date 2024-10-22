package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmServiceDemandRepositoryCustom;
import com.a1stream.domain.entity.CmmServiceDemand;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmServiceDemandRepository extends JpaExtensionRepository<CmmServiceDemand, Long>,CmmServiceDemandRepositoryCustom {

    List<CmmServiceDemand> findByServiceCategory(String serviceCategoryId);
    List<CmmServiceDemand> findAllByOrderByBaseDateAfter();
}
