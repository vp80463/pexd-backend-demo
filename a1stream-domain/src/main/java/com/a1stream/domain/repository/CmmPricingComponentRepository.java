package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmPricingComponent;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmPricingComponentRepository extends JpaExtensionRepository<CmmPricingComponent, Long> {

}
