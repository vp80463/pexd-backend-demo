package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.SalesOrderCancelHistoryRepositoryCustom;
import com.a1stream.domain.entity.SalesOrderCancelHistory;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface SalesOrderCancelHistoryRepository extends JpaExtensionRepository<SalesOrderCancelHistory, Long>, SalesOrderCancelHistoryRepositoryCustom{

}
