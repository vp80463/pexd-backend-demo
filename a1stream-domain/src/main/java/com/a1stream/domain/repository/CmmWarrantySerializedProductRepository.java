package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmWarrantySerializedProduct;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmWarrantySerializedProductRepository extends JpaExtensionRepository<CmmWarrantySerializedProduct, Long> {

    CmmWarrantySerializedProduct findFirstBySerializedProductId(Long serializedProductId);

    List<CmmWarrantySerializedProduct> findBySerializedProductIdIn(Set<Long> serializedProductId);
}
