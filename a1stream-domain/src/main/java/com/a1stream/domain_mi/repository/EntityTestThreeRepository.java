package com.a1stream.domain_mi.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain_mi.custom.EntityTestThreeRepositoryCustom;
import com.a1stream.domain_mi.entity.EntityTestThree;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;

@Repository
public interface EntityTestThreeRepository extends JpaExtensionRepository<EntityTestThree, Long>, EntityTestThreeRepositoryCustom{

    EntityTestThree findByStringTest(String string);

}
