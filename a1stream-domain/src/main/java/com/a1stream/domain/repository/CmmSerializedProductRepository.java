package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.CmmSerializedProductRepositoryCustom;
import com.a1stream.domain.entity.CmmSerializedProduct;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmSerializedProductRepository extends JpaExtensionRepository<CmmSerializedProduct, Long>,CmmSerializedProductRepositoryCustom{

    CmmSerializedProduct findFirstByFrameNo(String frameNo);

    CmmSerializedProduct findFirstByPlateNo(String plateNo);

    List<CmmSerializedProduct> findBySerializedProductIdIn(Set<Long> cmmSerialProIds);

    CmmSerializedProduct findBySerializedProductId(Long cmmSerializedProductId);

    List<CmmSerializedProduct> findByFrameNoIn(Set<String> frameNos);

    List<CmmSerializedProduct> findBySerializedProductIdInAndSalesStatus(Set<Long> serializedProductIds, String salesStatus);
}


