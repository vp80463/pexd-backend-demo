package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;
import com.a1stream.domain.entity.SpStockAccount;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface SpStockAccountRepository extends JpaExtensionRepository<SpStockAccount, String>{

}
