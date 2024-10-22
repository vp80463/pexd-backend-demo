package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ServiceOrderEditHistoryRepositoryCustom;
import com.a1stream.domain.entity.ServiceOrderEditHistory;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServiceOrderEditHistoryRepository extends JpaExtensionRepository<ServiceOrderEditHistory, Long>, ServiceOrderEditHistoryRepositoryCustom{

}
