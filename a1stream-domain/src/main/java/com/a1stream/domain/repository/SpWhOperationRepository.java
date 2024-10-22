package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.SpWhOperation;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface SpWhOperationRepository extends JpaExtensionRepository<SpWhOperation, String> {

}
