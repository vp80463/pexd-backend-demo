package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.SpPurchaseDw;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface SpPurchaseDwRepository extends JpaExtensionRepository<SpPurchaseDw, String> {

}
