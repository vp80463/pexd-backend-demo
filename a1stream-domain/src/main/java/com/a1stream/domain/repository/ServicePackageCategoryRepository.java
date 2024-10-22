package com.a1stream.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ServicePackageCategoryRepositoryCustom;
import com.a1stream.domain.entity.ServicePackageCategory;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ServicePackageCategoryRepository extends JpaExtensionRepository<ServicePackageCategory, Long>, ServicePackageCategoryRepositoryCustom {

    List<ServicePackageCategory> findBySiteIdAndServicePackageId(String siteId, Long servicePackageId);
}
