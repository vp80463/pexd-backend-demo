package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.SpReceiveDw;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface SpReceiveDwRepository extends JpaExtensionRepository<SpReceiveDw, String> {

}
