package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.DemandForecast;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface DemandForecastRepository extends JpaExtensionRepository<DemandForecast, Long> {

}
