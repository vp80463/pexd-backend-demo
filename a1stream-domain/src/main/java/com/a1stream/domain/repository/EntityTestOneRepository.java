package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.EntityTestOne;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface EntityTestOneRepository extends JpaExtensionRepository<EntityTestOne, Long> {

}
