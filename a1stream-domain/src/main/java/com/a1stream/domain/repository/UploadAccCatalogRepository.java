package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.UploadAccCatalog;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface UploadAccCatalogRepository extends JpaExtensionRepository<UploadAccCatalog, Long> {

    UploadAccCatalog findByParameterTypeId(String parameterTypeId);

}
