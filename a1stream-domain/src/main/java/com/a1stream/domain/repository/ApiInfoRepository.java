package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ApiInfo;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ApiInfoRepository extends JpaExtensionRepository<ApiInfo, Long> {

}
