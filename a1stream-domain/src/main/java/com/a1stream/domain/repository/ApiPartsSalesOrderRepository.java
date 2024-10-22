package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ApiPartsSalesOrder;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ApiPartsSalesOrderRepository extends JpaExtensionRepository<ApiPartsSalesOrder, Long> {

}
