package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.SerializedProductRepositoryCustom;
import com.a1stream.domain.entity.SerializedProduct;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;

@Repository
public interface SerializedProductRepository extends JpaExtensionRepository<SerializedProduct, Long>, SerializedProductRepositoryCustom {

    List<SerializedProduct> findByCmmSerializedProductIdIn(Set<Long> cmmSerialProIds);

    SerializedProduct findFirstByCmmSerializedProductIdAndSiteId(Long cmmSerialProId, String siteId);

    SerializedProduct findBySiteIdAndFrameNo(String siteId, String frameNo);

    SerializedProduct findFirstByFacilityIdAndFrameNo(Long facilityId, String frameNo);

    List<SerializedProduct> findByFacilityIdAndFrameNoIn(Long facilityId, Set<String> frameNoSet);

    List<SerializedProduct> findByCmmSerializedProductIdInAndSiteId(Set<Long> cmmSerialProIds, String siteId);

    List<SerializedProduct> findBySiteIdAndFrameNoIn(String siteId, Set<String> frameNoSet);

    List<SerializedProduct> findBySiteIdInAndFrameNoIn(Set<String> siteIdSet, Set<String> frameNoSet);

    List<SerializedProduct> findBySerializedProductIdInAndSalesStatus(List<Long>serializedProductIds, String salesStatus);

    List<SerializedProduct> findBySerializedProductIdIn(Set<Long> serialProIds);

    List<SerializedProduct> findBySiteId(String siteId);

    List<SerializedProduct> findBySerializedProductIdInAndFacilityId(Set<Long> serialProIds, Long facilityId);

    List<SerializedProduct> findByFrameNoIn(Set<String> frameNos);

    SerializedProduct findBySerializedProductId(Long serializedProductId);
}
