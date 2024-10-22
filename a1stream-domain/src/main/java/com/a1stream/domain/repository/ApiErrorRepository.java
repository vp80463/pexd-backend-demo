package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.ApiError;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ApiErrorRepository extends JpaExtensionRepository<ApiError, Long> {

}
