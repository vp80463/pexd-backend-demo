package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ServicePdiRepositoryCustom;
import com.a1stream.domain.entity.ServicePdi;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServicePdiRepository extends JpaExtensionRepository<ServicePdi, Long>, ServicePdiRepositoryCustom {

}
