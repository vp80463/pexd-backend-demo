package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmPersonRelation;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmPersonRelationRepository extends JpaExtensionRepository<CmmPersonRelation, Long> {

}
