package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ServicePdiItem;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServicePdiItemRepository extends JpaExtensionRepository<ServicePdiItem, Long> {

}
