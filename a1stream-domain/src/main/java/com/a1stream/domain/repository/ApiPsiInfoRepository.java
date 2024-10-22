package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ApiPsiInfo;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ApiPsiInfoRepository extends JpaExtensionRepository<ApiPsiInfo, Long> {

}
