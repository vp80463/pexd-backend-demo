package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.StockLocationTransaction;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface StockLocationTransactionRepository extends JpaExtensionRepository<StockLocationTransaction, Long> {


}
