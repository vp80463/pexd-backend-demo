package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ServiceRequestEditHistoryRepositoryCustom;
import com.a1stream.domain.entity.ServiceRequestEditHistory;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServiceRequestEditHistoryRepository extends JpaExtensionRepository<ServiceRequestEditHistory, Long>, ServiceRequestEditHistoryRepositoryCustom {

}
