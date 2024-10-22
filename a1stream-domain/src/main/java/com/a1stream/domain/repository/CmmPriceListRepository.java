package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmPriceList;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmPriceListRepository extends JpaExtensionRepository<CmmPriceList, Long> {

}
