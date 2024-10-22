package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ProductTaxRepositoryCustom;
import com.a1stream.domain.entity.ProductTax;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ProductTaxRepository extends JpaExtensionRepository<ProductTax, Long>, ProductTaxRepositoryCustom{

    List<ProductTax> findByProductIdInAndProductClassification(Set<Long> productIdList, String productClassification);

    ProductTax findByProductIdAndProductClassification(Long productId, String productClassification);

    List<ProductTax> findByProductIdIn(Set<Long> productIdList);

}
