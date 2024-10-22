package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ApiPartsSalesOrderItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ApiPartsSalesOrderItemRepository extends JpaExtensionRepository<ApiPartsSalesOrderItem, Long> {

}
