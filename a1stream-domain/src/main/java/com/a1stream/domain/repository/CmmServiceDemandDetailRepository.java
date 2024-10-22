package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.CmmServiceDemandDetail;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface CmmServiceDemandDetailRepository extends JpaExtensionRepository<CmmServiceDemandDetail, Long> {

    List<CmmServiceDemandDetail> findBySerializedProductId(Long serializedProductId);

    List<CmmServiceDemandDetail> findBySerializedProductIdIn(Set<Long> serializedProductIds);
}
