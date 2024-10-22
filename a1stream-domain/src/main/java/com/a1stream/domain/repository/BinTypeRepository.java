package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.entity.BinType;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface BinTypeRepository extends JpaExtensionRepository<BinType, Long> {

    List<BinType> findBySiteIdOrderByDescription(String siteId);

    List<BinType> findBySiteId(String siteId);

    List<BinType> findBySiteIdOrderByBinTypeCd(String siteId);

    BinType findBySiteIdAndBinTypeId(String siteId,Long binTypeId);

    List<BinType> findBySiteIdAndBinTypeCdIn(String siteId, Set<String> binTypeCds);

    List<BinType> findBySiteIdAndDescriptionIn(String siteId, Set<String> descriptions);
}
