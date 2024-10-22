package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;
import com.a1stream.domain.entity.SpSalesDw;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface SpSalesDwRepository extends JpaExtensionRepository<SpSalesDw, String>{

}
