package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.EntityTestTwoRepositoryCustom;
import com.a1stream.domain.entity.EntityTestTwo;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface EntityTestTwoRepository extends JpaExtensionRepository<EntityTestTwo, Long>, EntityTestTwoRepositoryCustom {

    public EntityTestTwo findByStringTest(String stringTest);
}
