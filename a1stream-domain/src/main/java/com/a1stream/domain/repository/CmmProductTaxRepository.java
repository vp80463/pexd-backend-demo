package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmProductTax;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmProductTaxRepository extends JpaExtensionRepository<CmmProductTax, Long> {

}
