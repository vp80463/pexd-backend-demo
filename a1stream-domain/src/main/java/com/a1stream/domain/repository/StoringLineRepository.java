package com.a1stream.domain.repository;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.StoringLineRepositoryCustom;
import com.a1stream.domain.entity.StoringLine;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface StoringLineRepository extends JpaExtensionRepository<StoringLine, Long>, StoringLineRepositoryCustom {

    List<StoringLine> findByStoringListId(Long storingListId);

    List<StoringLine> findByStoringLineIdIn(Set<Long> storingLineIds);

    StoringLine findFirstBySiteIdAndFacilityIdAndStoringLineNo(String siteId, Long facilityId, String storingLineNo);

    StoringLine findFirstByStoringLineId(Long storingLineId);
}
