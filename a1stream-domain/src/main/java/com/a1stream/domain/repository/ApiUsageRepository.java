package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ApiUsage;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ApiUsageRepository extends JpaExtensionRepository<ApiUsage, Long> {

}
