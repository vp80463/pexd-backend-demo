package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.PartsRopqParameter;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface PartsRopqParameterRepository extends JpaExtensionRepository<PartsRopqParameter, Long> {

}
