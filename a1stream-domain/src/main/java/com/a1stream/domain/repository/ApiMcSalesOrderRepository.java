package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ApiMcSalesOrder;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ApiMcSalesOrderRepository extends JpaExtensionRepository<ApiMcSalesOrder, Long> {

}
