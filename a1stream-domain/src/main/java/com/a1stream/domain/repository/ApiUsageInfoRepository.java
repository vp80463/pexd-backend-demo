package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ApiUsageInfo;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ApiUsageInfoRepository extends JpaExtensionRepository<ApiUsageInfo, Long> {

}
