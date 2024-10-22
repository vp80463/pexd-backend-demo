package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ServicePackageRepositoryCustom;
import com.a1stream.domain.entity.ServicePackage;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServicePackageRepository extends JpaExtensionRepository<ServicePackage, Long>, ServicePackageRepositoryCustom {

    ServicePackage findByServicePackageId(Long servicePackageId);
}
