package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmServiceHistoryRepositoryCustom;
import com.a1stream.domain.entity.CmmServiceHistory;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmServiceHistoryRepository extends JpaExtensionRepository<CmmServiceHistory, Long>, CmmServiceHistoryRepositoryCustom {

}
